package web.thaiticketmajor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.Category;
import web.thaiticketmajor.Models.Concert;
import web.thaiticketmajor.Models.ConcertZone;
import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Models.Zone;
import web.thaiticketmajor.Repositories.ConcertRepository;
import web.thaiticketmajor.Repositories.ConcertZoneRepository;
import web.thaiticketmajor.Repositories.SeatRepository;
import web.thaiticketmajor.Services.CategoryService;
import web.thaiticketmajor.Services.ConcertService;
import web.thaiticketmajor.Services.ConcertZoneService;
import web.thaiticketmajor.Services.SeatService;
import web.thaiticketmajor.Services.ZoneService;
import web.thaiticketmajor.dto.request.ChangeZoneRequest;

import java.util.stream.Collectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.function.Consumer;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

import java.util.List;
import java.util.ArrayList;

import java.util.Optional;

@Controller
@Slf4j
public class ConcertController {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private ConcertZoneService concertZoneService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertZoneRepository concertZoneRepository;

    @Autowired
    private SeatService seatService;

    @GetMapping({
            "/concert",
            "/concert/list"
    })
    public String getList(Model model) {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // String email = authentication.getName();

        // model.addAttribute("email", email);
        List<Concert> list = concertService.dsConcert();

        model.addAttribute("ds", list);
        model.addAttribute("content", "admin/pages/concert-manager.html"); // duyet.html

        return "admin/index.html";

    }

    @GetMapping("/addConcertAndSeats")
    public String showAddConcertForm(Model model) {
        List<Zone> listZone = zoneService.getAllZones();
        model.addAttribute("listZone", listZone); // Đảm bảo tên thuộc tính là "listZone"
        model.addAttribute("concert", new Concert());
        model.addAttribute("listCategory", this.categoryService.dsCategory());
        model.addAttribute("content", "admin/pages/add-concert.html");
        return "admin/index.html";
    }

    @PostMapping("/addConcertAndSeats")
    public String addConcertAndSeats(
            Concert concert,
            int rows,
            int columns,
            @RequestParam("category_id") Integer categoryId,
            @RequestParam("mainImageFile") MultipartFile mainImageFile,
            @RequestParam("subImageFile1") MultipartFile subImageFile1,
            @RequestParam("subImageFile2") MultipartFile subImageFile2,
            @RequestParam(value = "zoneIds", required = false) List<Integer> zoneIds,
            @RequestParam(value = "prices", required = false) List<Double> prices,
            Model model) {

        Category category = categoryService.tìmCategoryTheoId(categoryId);
        if (category != null) {
            concert.setCategory(category);
        }

        String folder = "src/main/resources/static/images/";
        Path folderPath = Paths.get(folder);
        // Tạo thư mục nếu không tồn tại
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                log.error("Error creating directory: {}", e.getMessage());
                model.addAttribute("errorMessage", "Không thể tạo thư mục lưu ảnh.");
                return "seat.html"; // Quay lại form thêm concert
            }
        }

        try {
            // Lưu hình ảnh
            saveImage(mainImageFile, concert::setMainImage, folder);
            saveImage(subImageFile1, concert::setSubImage1, folder);
            saveImage(subImageFile2, concert::setSubImage2, folder);
        } catch (IOException e) {
            log.error("Error saving images: {}", e.getMessage());
            model.addAttribute("errorMessage", "Không thể lưu ảnh. Vui lòng thử lại.");
            model.addAttribute("concert", concert);
            model.addAttribute("listCategory", this.categoryService.dsCategory());
            return "seat.html"; // Quay lại form thêm concert
        }
        concert.setCreated_at(LocalDate.now());
        Concert savedConcert = concertRepository.save(concert);

        if (zoneIds != null && prices != null) {
            for (int i = 0; i < zoneIds.size(); i++) {
                Integer zoneId = zoneIds.get(i);
                Double price = prices.get(i);

                Zone zone = zoneService.getZoneById(zoneId);
                if (zone != null) {
                    ConcertZone concertZone = new ConcertZone();
                    concertZone.setConcert_id(savedConcert.getId());
                    concertZone.setZone_id(zone.getId());
                    concertZone.setPrice(price);

                    concertZoneService.save(concertZone); // Lưu từng ConcertZone vào database
                }
            }
        }
        savedConcert = concertService.addConcertAndSeats(concert, rows, columns);

        return "redirect:/concert/list";
    }

    @GetMapping("/concert/update/{id}")
    public String updateConcert(@PathVariable int id, Model model) {
        Concert concert = concertService.getConcertById(id);
        List<Seat> seats = concertService.getSeatsByConcertId(id);
        List<Zone> listZone = zoneService.getAllZones();
        List<ConcertZone> concertZoneList = concertZoneService.getConcertZonesByConcertId(id);

        // Tạo một Map để lưu trữ giá cho từng zone
        Map<Integer, Double> concertZonePrices = new HashMap<>();
        for (ConcertZone cz : concertZoneList) {
            concertZonePrices.put(cz.getZone().getId(), cz.getPrice());
        }

        model.addAttribute("concert", concert);
        model.addAttribute("concertZones", concertZoneList);
        model.addAttribute("seats", seats);
        model.addAttribute("listZone", listZone);
        model.addAttribute("concertZonePrices", concertZonePrices); // Thêm Map vào model
        model.addAttribute("listCategory", this.categoryService.dsCategory());
        model.addAttribute("content", "admin/pages/update-concert.html");

        return "admin/index.html";
    }

    // Hiển thị chi tiết concert
    @GetMapping("/concert/detail/{id}")
    public String showConcertDetail(@PathVariable int id, Model model) {
        Concert concert = concertService.getConcertById(id);
        List<Seat> seats = concertService.getSeatsByConcertId(id);
        model.addAttribute("concert", concert);
        model.addAttribute("seats", seats);
        model.addAttribute("content", "user/html/detailConcert.html");
        return "layout.html"; // Trả về tệp concertDetail.html
    }

    @GetMapping("/concert/xem/{id}/map")
    public String getMap(Model model, @PathVariable int id) {

        Concert concert = concertService.getConcertById(id);
        List<Seat> seats = concertService.getSeatsByConcertId(id);
        List<Zone> listZone = zoneService.getAllZones();
        List<ConcertZone> concertZoneList = concertZoneService.getConcertZonesByConcertId(id);

        model.addAttribute("concert", concert);
        model.addAttribute("concertZones", concertZoneList);
        model.addAttribute("seats", seats);
        model.addAttribute("listZone", listZone);

        model.addAttribute("content", "user/html/seatmap.html");

        return "layout.html";
    }

    @PostMapping("/concert/updateSeats")
    public String updateSeats(@RequestBody List<Integer> selectedSeatIds, Model model) {
        log.info("updateSeats method called");
        log.info("Selected seat IDs: " + selectedSeatIds);

        if (selectedSeatIds.isEmpty()) {
            return "redirect:/concertDetail";
        }

        Integer concertId = null; // Biến để lưu concertId

        for (int seatId : selectedSeatIds) {
            Optional<Seat> seatOptional = seatRepository.findById(seatId);
            if (seatOptional.isPresent()) {
                Seat seat = seatOptional.get();
                seat.setStatus(!seat.isStatus());
                seatRepository.save(seat);
                if (concertId == null) {
                    concertId = seat.getConcert_id(); // Lấy concertId của ghế đầu tiên
                }
            }
        }

        if (concertId != null) {
            List<Seat> allSeats = seatRepository.findByConcertId(concertId); // Lấy tất cả ghế của concert
            model.addAttribute("seats", allSeats);
        }

        return "admin/pages/update-concert :: seats"; // Trả về fragment seats
    }

    @PostMapping("/concert/changeZone")
    public String changeZone(@RequestBody ChangeZoneRequest requestData, Model model) {
        List<Integer> selectedSeatIds = requestData.getSelectedSeatIds();
        int selectedZoneId = requestData.getSelectedZoneId();

        Integer concertId = null; // Biến để lưu concertId

        for (int seatId : selectedSeatIds) {
            Optional<Seat> seatOptional = seatRepository.findById(seatId);
            if (seatOptional.isPresent()) {
                Seat seat = seatOptional.get();
                seat.setConcertZone_id(selectedZoneId);
                ConcertZone newConcertZone = concertZoneRepository.findById(selectedZoneId).orElse(null);
                if (newConcertZone != null) {
                    seat.setConcertZone(newConcertZone); // Cập nhật đối tượng ConcertZone
                }
                seatRepository.save(seat);
                if (concertId == null) {
                    concertId = seat.getConcert_id(); // Lấy concertId của ghế đầu tiên
                }
            }
        }

        if (concertId != null) {
            List<Seat> allSeats = seatRepository.findByConcertId(concertId); // Lấy tất cả ghế của concert
            model.addAttribute("seats", allSeats);
        }

        return "admin/pages/update-concert :: seats"; // Trả về fragment seats
    }

//     @GetMapping("/concert/payment")
// public String showPaymentPage(HttpServletRequest request, Model model) {
//     // Lấy danh sách ghế đã chọn từ sessionStorage
//     String selectedSeatIdsStr = (String) request.getSession().getAttribute("selectedSeatIds");
    
//     // Chuyển chuỗi seatIds thành mảng các ID ghế
//     String[] seatIdsArray = selectedSeatIdsStr.split(",");
    
//     List<Seat> listSeat = new ArrayList<>();
//     for (String seatId : seatIdsArray) {
//         // Tìm từng ghế theo seatId
//         Seat seat = seatService.findById(Integer.parseInt(seatId));
//         if (seat != null) {
//             listSeat.add(seat);
//         }
//     }

//     // Thêm danh sách ghế vào model để hiển thị trong view
//     model.addAttribute("listSeat", listSeat);

//     return "user/html/payment"; // Trả về trang thanh toán (Thymeleaf template)
// }







@PostMapping("/concert/payment")
public String handlePayment(@RequestBody Map<String, List<Integer>> requestBody, HttpSession session) {
    // Lấy danh sách seatIds từ body của request
    List<Integer> seatIds = requestBody.get("seatIds");

    if (seatIds == null || seatIds.isEmpty()) {
        return "errorPage";  // Trang lỗi nếu không có ghế nào được chọn
    }

    // Lưu ghế vào session
    session.setAttribute("selectedSeatIds", seatIds);

    return "redirect:/concert/payment";  // Chuyển hướng tới trang thanh toán
}

@GetMapping("/concert/payment")
public String showPaymentPage(HttpSession session, Model model) {
    // Lấy danh sách seatIds từ session
    List<Integer> seatIds = (List<Integer>) session.getAttribute("selectedSeatIds");

    if (seatIds == null || seatIds.isEmpty()) {
        return "errorPage";  // Trang lỗi nếu không có ghế nào được chọn
    }


    List<Seat> listSeat = new ArrayList<>();
    for (Integer seatId : seatIds) {
        // Tìm từng ghế theo seatId
        Seat seat = seatService.findById(seatId);
        if (seat != null) {
            listSeat.add(seat);
        }
    }

    double totalAmount = listSeat.stream()
        .mapToDouble(Seat::getPrice)  // Lấy giá tiền của từng ghế
        .sum();  // Tính tổng

    // Thêm tổng tiền vào model
    model.addAttribute("totalAmount", totalAmount);

    // Thêm danh sách ghế vào model để hiển thị trong view
    model.addAttribute("listSeat", listSeat);

    return "user/html/payment";  // Trả về trang thanh toán
}




    @PostMapping("/concert/update")
    public String updateConcert(
            @ModelAttribute("concert") Concert concert,
            @RequestParam(required = false) List<Integer> zoneIds, // Danh sách zone được chọn
            @RequestParam(required = false) List<Double> prices,
            @RequestParam("mainImageFile") MultipartFile mainImageFile,
            @RequestParam("subImageFile1") MultipartFile subImageFile1,
            @RequestParam("subImageFile2") MultipartFile subImageFile2, // Danh sách giá tương ứng với zone
            RedirectAttributes redirectAttributes) {

        String folder = "src/main/resources/static/images/";
        Path folderPath = Paths.get(folder);

        // Tạo thư mục nếu không tồn tại
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                log.error("Error creating directory: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("THONG_BAO_ERROR", "Không thể tạo thư mục lưu ảnh.");
                return "redirect:/concerts"; // Quay lại danh sách concert
            }
        }

        Concert existingConcert = concertService.getConcertById(concert.getId());

        // Kiểm tra và lưu file chính
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            try {
                saveImage(mainImageFile, concert::setMainImage, folder);
            } catch (IOException e) {
                log.error("Error saving main image: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("THONG_BAO_ERROR", "Không thể lưu ảnh chính. Vui lòng thử lại.");
                return "redirect:/concerts"; // Quay lại danh sách concert
            }
        } else {
            // Nếu không chọn file mới, giữ lại file cũ
            concert.setMainImage(existingConcert.getMainImage());
        }

        // Kiểm tra và lưu file phụ 1
        if (subImageFile1 != null && !subImageFile1.isEmpty()) {
            try {
                saveImage(subImageFile1, concert::setSubImage1, folder);
            } catch (IOException e) {
                log.error("Error saving sub image 1: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("THONG_BAO_ERROR", "Không thể lưu ảnh phụ 1. Vui lòng thử lại.");
                return "redirect:/concerts"; // Quay lại danh sách concert
            }
        } else {
            // Nếu không chọn file mới, giữ lại file cũ
            concert.setSubImage1(existingConcert.getSubImage1());
        }

        // Kiểm tra và lưu file phụ 2
        if (subImageFile2 != null && !subImageFile2.isEmpty()) {
            try {
                saveImage(subImageFile2, concert::setSubImage2, folder);
            } catch (IOException e) {
                log.error("Error saving sub image 2: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("THONG_BAO_ERROR", "Không thể lưu ảnh phụ 2. Vui lòng thử lại.");
                return "redirect:/concerts"; // Quay lại danh sách concert
            }
        } else {
            // Nếu không chọn file mới, giữ lại file cũ
            concert.setSubImage2(existingConcert.getSubImage2());
        }

        concert.setCreated_at(concert.getCreated_at());
        concert.setUpdate_at(LocalDate.now());
        concertService.updateConcert(concert);

        // Tìm ID của zone có tên "standard"
        int standardZoneId = zoneService.getZoneIdByName("standard");
        ConcertZone standardConcertZoneId = concertZoneRepository.findByConcertIdAndZoneId(concert.getId(),
                standardZoneId);
        // Lấy danh sách các ConcertZone hiện có cho concert
        List<ConcertZone> existingConcertZones = concertZoneService.getConcertZonesByConcertId(concert.getId());

        // Tạo một tập hợp các zoneId đã chọn để dễ dàng kiểm tra
        Set<Integer> selectedZoneIds = new HashSet<>(zoneIds != null ? zoneIds : Collections.emptyList());

        // Xử lý cập nhật hoặc xóa ConcertZone
        for (ConcertZone existingConcertZone : existingConcertZones) {
            Integer zoneId = existingConcertZone.getZone_id();

            if (selectedZoneIds.contains(zoneId)) {
                // Nếu zoneId đã được chọn, cập nhật giá
                int index = zoneIds.indexOf(zoneId);
                if (index != -1 && prices.size() > index) {
                    Double price = prices.get(index);
                    existingConcertZone.setPrice(price);
                    concertZoneService.save(existingConcertZone); // Cập nhật vào cơ sở dữ liệu
                }
            } else {
                // Nếu zoneId không được chọn, xóa ConcertZone

                // Kiểm tra xem có ghế nào có zone đó không
                List<Seat> seats = seatRepository.findByConcertZoneId(existingConcertZone.getId());
                for (Seat seat : seats) {
                    // Cập nhật concertZone_id của ghế thành concertZone_id của zone "standard"
                    seat.setConcertZone_id(standardConcertZoneId.getId());
                    seatRepository.save(seat); // Lưu ghế
                }

                concertZoneService.deleteConcertZone(existingConcertZone.getId());
            }
        }

        // Thêm mới các ConcertZone nếu cần
        if (zoneIds != null && prices != null) {
            for (int i = 0; i < zoneIds.size(); i++) {
                Integer zoneId = zoneIds.get(i);
                Double price = prices.get(i);

                // Kiểm tra nếu ConcertZone đã tồn tại thì bỏ qua
                boolean exists = existingConcertZones.stream()
                        .anyMatch(cz -> cz.getZone_id() == zoneId);

                if (!exists) {
                    // Nếu không tồn tại thì thêm mới
                    ConcertZone newConcertZone = new ConcertZone();
                    newConcertZone.setConcert_id(concert.getId());
                    newConcertZone.setZone_id(zoneId);
                    newConcertZone.setPrice(price);
                    concertZoneService.save(newConcertZone); // Lưu vào cơ sở dữ liệu
                }
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Concert đã được cập nhật thành công.");
        return "redirect:/concert/list"; // Chuyển hướng về danh sách concert sau khi cập nhật
    }


    // Phương thức để xóa ảnh cũ
    private void deleteOldImage(String folder, String oldImageName) {
        if (oldImageName != null && !oldImageName.isEmpty()) {
            Path oldImagePath = Paths.get(folder + oldImageName);
            try {
                if (Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                }
            } catch (IOException e) {
                log.error("Error deleting old image: {}", e.getMessage());
            }
        }
    }

    // Phương thức để lưu ảnh mới
    private void saveImage(MultipartFile file, Consumer<String> setter, String folder) throws IOException {
        if (!file.isEmpty()) {
            Path newPath = Paths.get(folder + file.getOriginalFilename());
            Files.write(newPath, file.getBytes());
            setter.accept(file.getOriginalFilename());
        }
    }

    @PostMapping("/concert/delete")
    @ResponseBody // Để trả về dữ liệu JSON
    public ResponseEntity<String> deleteConcert(@RequestParam int id) {
        log.info("Deleting concert with id: {}", id);
        concertService.deleteConcertAndSeats(id);
        return ResponseEntity.ok("Concert deleted successfully");
    }

}

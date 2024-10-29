package web.thaiticketmajor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.Category;
import web.thaiticketmajor.Models.Concert;
import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Repositories.SeatRepository;
import web.thaiticketmajor.Services.CategoryService;
import web.thaiticketmajor.Services.ConcertService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.io.IOException;

import java.util.List;
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

        Concert savedConcert = concertService.addConcertAndSeats(concert, rows, columns);
        return "redirect:/concert/list";
    }

    @GetMapping("/concert/update/{id}")
    public String updateConcert(@PathVariable int id, Model model) {
        
        Concert concert = concertService.getConcertById(id);
        List<Seat> seats = concertService.getSeatsByConcertId(id);
        model.addAttribute("concert", concert);
        model.addAttribute("seats", seats);
        model.addAttribute("listCategory", this.categoryService.dsCategory());
        model.addAttribute("content", "admin/pages/update-concert.html"); // duyet.html

        return "admin/index.html";
    }

    // Hiển thị chi tiết concert
    @GetMapping("/concertDetail/{id}")
    public String showConcertDetail(@PathVariable int id, Model model) {
        Concert concert = concertService.getConcertById(id);
        List<Seat> seats = concertService.getSeatsByConcertId(id);
        model.addAttribute("concert", concert);
        model.addAttribute("seats", seats);
        return "concertdetail.html"; // Trả về tệp concertDetail.html
    }

    @PostMapping("/concert/updateSeats")
    public String updateSeats(@RequestBody List<Integer> selectedSeatIds) {
        log.info("updateSeats method called"); // Log để kiểm tra phương thức có được gọi không
        log.info("Selected seat IDs: " + selectedSeatIds); // Log giá trị của selectedSeatIds

        if (selectedSeatIds.isEmpty()) {
            return "redirect:/concertDetail"; // Nếu không có ghế nào được chọn
        }

        for (int seatId : selectedSeatIds) {
            Optional<Seat> seatOptional = seatRepository.findById(seatId);
            if (seatOptional.isPresent()) {
                Seat seat = seatOptional.get();
                // Đổi trạng thái ghế
                seat.setStatus(!seat.isStatus());
                seatRepository.save(seat); // Lưu ghế
            }
        }

        return "redirect:/concertDetail"; // Redirect về trang chi tiết của concert
    }

    @PostMapping("/concert/update")
public String updateConcert(
        @ModelAttribute("Concert") Concert concert, 
        RedirectAttributes redirectAttributes) {

    // Lấy concert từ database bằng ID
    concertService.updateConcert(concert);
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
    public String deleteConcert(@RequestParam int id) {
        log.info("Deleting concert with id: {}", id);
        concertService.deleteConcertAndSeats(id);
        return "redirect:/concert/list"; // Chuyển hướng về danh sách concert sau khi xóa
    }

}

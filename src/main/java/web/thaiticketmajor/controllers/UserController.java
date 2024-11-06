package web.thaiticketmajor.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.Role;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Models.Concert;
import web.thaiticketmajor.Services.AuthenticationService;
import web.thaiticketmajor.Services.ConcertService;
import web.thaiticketmajor.Services.RoleService;
import web.thaiticketmajor.Services.UserService;
import web.thaiticketmajor.dto.request.AuthenticationRequest;
import web.thaiticketmajor.dto.response.AuthenticationResponse;
import web.thaiticketmajor.exception.AppException;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ConcertService concertService;

    @GetMapping({
            "/admin",
            "/admin/duyet"
    })
    public String getDuyet(Model model) {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // String email = authentication.getName();

        // model.addAttribute("email", email);
        User dl = new User();

        model.addAttribute("dl", dl);
        model.addAttribute("listRole", this.roleService.getAll());
        List<User> list = userService.duyệtUser();

        model.addAttribute("ds", list);
        model.addAttribute("content", "admin/pages/user-manager.html"); // duyet.html

        return "admin/index.html";

    }

    // @GetMapping("/user/them")
    // public String getThem(Model model) {
    // User dl = new User();

    // model.addAttribute("dl", dl);
    // model.addAttribute("listRole", this.roleService.getAll());
    // model.addAttribute("content", "user/sua");
    // return "admin/pages/add-user.html";
    // }

    @GetMapping("/admin/update-user")
    public String getSua(Model model, @RequestParam("id") int id) {
        var dl = userService.xemUser(id);

        model.addAttribute("dl", dl);
        model.addAttribute("listRole", this.roleService.getAll());
        model.addAttribute("action", "/user/sua");

        return "admin/pages/update_user.html";
    }

    @GetMapping("/admin/user-delete")
    public String getXoa(Model model, @RequestParam(value = "id") int id) {
        User user = userService.tìmUserTheoId(id);

        model.addAttribute("dl", user);
        model.addAttribute("content", "user/xoa.html");

        return "layout.html";
    }

    @GetMapping("/user/xem/{id}")
    public String getXem(Model model, @PathVariable(value = "id") int id) {
        User user = userService.xemUser(id);
        model.addAttribute("dl", user);
        model.addAttribute("content", "user/xem.html");

        return "layout.html";
    }

    @GetMapping("/index")
    public String getMethodName(Model model) {
        List<Concert> list = concertService.dsConcert();
        model.addAttribute("ds", list);
        model.addAttribute("content", "user/html/concerts.html");
        return "layout.html";
    }

    @GetMapping("/admin/login")
    public String vaotrang(Model model) {
        User user = new User();
        model.addAttribute("loginRequest", user);

        return "admin/pages/login-admin.html";
    }

    @PostMapping("/user/login")
    public String login(@Valid @ModelAttribute("loginRequest") AuthenticationRequest request, Model model,
            HttpServletResponse response, HttpServletRequest httpRequest, RedirectAttributes redirectAttributes) {
        log.info("Vào phương thức login với người dùng: {}", request.getEmail());
        try {
            // Gọi service để thực hiện xác thực
            AuthenticationResponse authResponse = authenticationService.authenticate(request);

            // Thêm log để ghi nhận thông tin người dùng
            log.info("Đăng nhập thành công cho người dùng: {}", request.getEmail());

            log.info("Token nhận được: {}", authResponse.getToken());
            User user = userService.findUserByEmail(request.getEmail());

            // Lưu token vào cookie
            Cookie cookie = new Cookie("auth_token", authResponse.getToken());
            cookie.setHttpOnly(true); // Giúp ngăn chặn JavaScript truy cập cookie
            cookie.setPath("/"); // Đảm bảo cookie có thể truy cập trên toàn bộ ứng dụng
            cookie.setMaxAge(3600); // Thời gian sống của cookie (1 giờ)
            response.addCookie(cookie); // Thêm cookie vào phản hồi HTTP

            if (user.getRole().getName().equals("USER")) {
                return "redirect:/index";
            } else {
                return "redirect:/category/duyet";
            }

        } catch (AppException e) {
            String refererUrl = httpRequest.getHeader("Referer");

            // Thêm thông báo lỗi để hiển thị trên trang trước đó
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác.");

            // Quay về trang trước đó nếu có URL, nếu không thì về trang login mặc định
            return "redirect:" + (refererUrl);
        }

    }

    @PostMapping("/admin/logout")
    public String logout(HttpServletResponse response, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        log.info("Thực hiện đăng xuất người dùng.");

        // Xóa cookie auth_token
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setPath("/"); // Đảm bảo cookie được xóa trên toàn bộ ứng dụng
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Thiết lập thời gian sống là 0 để xóa cookie
        response.addCookie(cookie); // Thêm cookie vào phản hồi để xóa

        // Xóa thông tin xác thực khỏi SecurityContext
        SecurityContextHolder.clearContext();
        // Ghi log để biết đã hoàn thành đăng xuất
        log.info("Đăng xuất thành công và đã xóa cookie auth_token.");

        // Thêm thông báo nếu cần thiết
        redirectAttributes.addFlashAttribute("logoutMessage", "Đăng xuất thành công!");

        // Chuyển hướng về trang đăng nhập hoặc trang chủ
        return "redirect:/admin/login";
    }

    @PostMapping("/admin/add-user")
    public String postThem(@ModelAttribute("User") User user) {
        // System.out.print("save action...");

        // @todo sửa chỗ này đi
        user.setCreated_at(LocalDate.now());
        // dl.setNgaySua(LocalDate.now());

        userService.saveUser(user);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        // Gửi thông báo thành công từ giao diện Thêm Mới sang giao diện Duyệt

        return "redirect:/admin/duyet";
    }

    @PostMapping("/admin/update-user")
    public String postSua(@ModelAttribute("User") User user, RedirectAttributes redirectAttributes) {

        // @todo sửa chỗ này đi
        user.setPassword(user.getPassword());
        user.setCreated_at(user.getCreated_at());
        user.setUpdate_at(LocalDate.now());

        userService.updateUser(user);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Đã sửa thành công !");

        return "redirect:/admin/duyet";
    }

    @PostMapping("/admin/delete-user")
    public String postXoa(Model model, @RequestParam("id") int id) // request param phải khớp với name="Id" của thẻ html
                                                                   // input
    {

        this.userService.xóaUser(id);
        return "redirect:/admin/duyet";
    }

    // User
    @GetMapping("/user/login")
    public String LoginUser(Model model) {
        User user = new User();
        model.addAttribute("loginRequest", user);

        return "user/html/login.html";
    }

    @PostMapping("/user/signup")
    public String postAdd(@ModelAttribute("User") User user) {

        Role role = roleService.findByName("USER");
        user.setCreated_at(LocalDate.now());
        user.setRole_id(role.getId());
        // dl.setNgaySua(LocalDate.now());

        userService.saveUser(user);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        // Gửi thông báo thành công từ giao diện Thêm Mới sang giao diện Duyệt

        return "redirect:/user/login";
    }

}// end class

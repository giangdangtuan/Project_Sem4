package web.thaiticketmajor.controllers;

import java.util.List;
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
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Services.AuthenticationService;
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

    @GetMapping({
            "/user",
            "/user/duyet"
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

    @GetMapping("/user/updateajax")
    public String getSua(Model model, @RequestParam("id") int id) {
        var dl = userService.xemUser(id);

        model.addAttribute("dl", dl);
        model.addAttribute("listRole", this.roleService.getAll());
        model.addAttribute("action", "/user/sua");

        return "admin/pages/update_user.html";
    }

    @GetMapping("/user/xoa")
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

    @PostMapping("/user/add")
    public String postThem(@ModelAttribute("User") User dl) {
        // System.out.print("save action...");

        // @todo sửa chỗ này đi
        // dl.setNgayTao(LocalDate.now());
        // dl.setNgaySua(LocalDate.now());

        userService.saveUser(dl);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        // Gửi thông báo thành công từ giao diện Thêm Mới sang giao diện Duyệt

        return "redirect:/user/duyet";
    }

    @PostMapping("/user/sua")
    public String postSua(@ModelAttribute("User") User user, RedirectAttributes redirectAttributes) {

        // @todo sửa chỗ này đi

        // dl.setNgaySua(LocalDate.now());

        userService.updateUser(user);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Đã sửa thành công !");

        return "redirect:/user/duyet";
    }

    @PostMapping("/user/delete")
    public String postXoa(Model model, @RequestParam("id") int id) // request param phải khớp với name="Id" của thẻ html
                                                                   // input
    {

        this.userService.xóaUser(id);
        return "redirect:/user/duyet";
    }

    @GetMapping("/user/login")
    public String vaotrang(Model model) {
        User user = new User();
        model.addAttribute("loginRequest", user);

        return "user/html/login.html";
    }

    @PostMapping("/user/login")
    public String login(@Valid @ModelAttribute("loginRequest") AuthenticationRequest request, Model model,
            HttpServletResponse response) {
        log.info("Vào phương thức login với người dùng: {}", request.getEmail());
        try {
            // Gọi service để thực hiện xác thực
            AuthenticationResponse authResponse = authenticationService.authenticate(request);

            // Thêm log để ghi nhận thông tin người dùng
            log.info("Đăng nhập thành công cho người dùng: {}", request.getEmail());

            log.info("Token nhận được: {}", authResponse.getToken());

            // Lưu token vào cookie
            Cookie cookie = new Cookie("auth_token", authResponse.getToken());
            cookie.setHttpOnly(true); // Giúp ngăn chặn JavaScript truy cập cookie
            cookie.setPath("/"); // Đảm bảo cookie có thể truy cập trên toàn bộ ứng dụng
            cookie.setMaxAge(3600); // Thời gian sống của cookie (1 giờ)
            response.addCookie(cookie); // Thêm cookie vào phản hồi HTTP
            log.info("Quyền của người dùng: {}",
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            // Chuyển hướng đến một trang khác
            return "redirect:/category/duyet"; // Ví dụ: chuyển đến trang home
        } catch (AppException e) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác.");
            return "user/html/login.html"; // Nếu không thành công, trở về form đăng nhập
        }
    }

    @PostMapping("/user/logout")
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
        return "redirect:/user/login";
    }

}// end class

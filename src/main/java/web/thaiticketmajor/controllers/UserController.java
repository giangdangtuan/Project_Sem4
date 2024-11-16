package web.thaiticketmajor.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.Role;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Models.Bill;
import web.thaiticketmajor.Models.Bill_detail;
import web.thaiticketmajor.Services.AuthenticationService;
import web.thaiticketmajor.Services.BillService;
import web.thaiticketmajor.Services.ConcertService;
import web.thaiticketmajor.Services.EmailService;
import web.thaiticketmajor.Services.RoleService;
import web.thaiticketmajor.Services.UserService;
import web.thaiticketmajor.dto.request.AuthenticationRequest;
import web.thaiticketmajor.dto.response.AuthenticationResponse;
import web.thaiticketmajor.exception.AppException;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private BillService billService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private EmailService emailService;

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_ACCOUNT')")
    @GetMapping({
            "/admin",
            "/admin/duyet"
    })
    public String getDuyet(Model model) {

        User dl = new User();

        model.addAttribute("dl", dl);
        model.addAttribute("listRole", this.roleService.getAll());
        List<User> list = userService.duyệtUser();

        model.addAttribute("ds", list);
        model.addAttribute("content", "admin/pages/user-manager.html");

        return "admin/index.html";

    }

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_ACCOUNT')")
    @GetMapping("/admin/update-user")
    public String getSua(Model model, @RequestParam("id") int id) {
        var dl = userService.xemUser(id);

        model.addAttribute("dl", dl);
        model.addAttribute("listRole", this.roleService.getAll());
        model.addAttribute("action", "/user/sua");

        return "admin/pages/update_user.html";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_ACCOUNT')")
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
            User user = userService.findUserByEmail(request.getEmail());

            if (user == null || !user.isEnabled()) {
                log.warn("Người dùng chưa được xác thực hoặc không tồn tại: {}", request.getEmail());
                redirectAttributes.addFlashAttribute("error", "Tài khoản của bạn chưa được xác thực.");
                return "redirect:/user/login";
            }

            AuthenticationResponse authResponse = authenticationService.authenticate(request);
            log.info("Đăng nhập thành công cho người dùng: {}", request.getEmail());

            log.info("Token nhận được: {}", authResponse.getToken());

            Cookie cookie = new Cookie("auth_token", authResponse.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            Cookie userIdCookie = new Cookie("user_id", String.valueOf(user.getId()));
            Cookie userEmailCookie = new Cookie("user_email", user.getEmail());

            userIdCookie.setMaxAge(3600);
            userEmailCookie.setMaxAge(3600);

            userIdCookie.setPath("/");
            userEmailCookie.setPath("/");

            response.addCookie(userIdCookie);
            response.addCookie(userEmailCookie);

            if (user.getRole().getName().equals("USER")) {
                return "redirect:/index";
            } else {
                return "redirect:/dashboard";
            }

        } catch (AppException e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage());
            String refererUrl = httpRequest.getHeader("Referer");

            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác.");

            return "redirect:" + (refererUrl);
        }

    }

    @PostMapping("/admin/logout")
    public void logout(HttpServletResponse response, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        log.info("Thực hiện đăng xuất người dùng.");

        // Lấy tất cả cookie từ request
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Tạo cookie mới với cùng tên để xóa cookie hiện tại
                Cookie cookieToDelete = new Cookie(cookie.getName(), null);
                cookieToDelete.setPath("/"); // Đảm bảo rằng cookie có đường dẫn giống nhau để xóa đúng
                cookieToDelete.setMaxAge(0); // Thiết lập thời gian sống bằng 0 để xóa cookie
                response.addCookie(cookieToDelete);
            }
        }

        // Xóa thông tin trong SecurityContext
        SecurityContextHolder.clearContext();

        log.info("Đăng xuất thành công và đã xóa tất cả cookie.");

        // Gửi thông báo thành công khi đăng xuất
        redirectAttributes.addFlashAttribute("logoutMessage", "Đăng xuất thành công!");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_ACCOUNT')")
    @PostMapping("/admin/add-user")
    public String postThem(@ModelAttribute("User") User user) {

        user.setCreated_at(LocalDate.now());

        userService.saveUser(user);

        return "redirect:/admin/duyet";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_ACCOUNT')")
    @PostMapping("/admin/update-user")
    public String postSua(@ModelAttribute("User") User user, RedirectAttributes redirectAttributes) {

        user.setPassword(user.getPassword());
        user.setCreated_at(user.getCreated_at());
        user.setUpdate_at(LocalDate.now());

        userService.updateUser(user);

        redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Đã sửa thành công !");

        return "redirect:/admin/duyet";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_ACCOUNT')")
    @PostMapping("/admin/delete-user")
    public String postXoa(Model model, @RequestParam("id") int id)

    {

        this.userService.xóaUser(id);
        return "redirect:/admin/duyet";
    }

    @GetMapping("/user/login")
    public String LoginUser(Model model) {
        User userLogin = new User();
        User userSignUp = new User();
        model.addAttribute("loginRequest", userLogin);
        model.addAttribute("signUpRequest", userSignUp);

        return "user/html/login.html";
    }

    @GetMapping("/user/profile")
    public String ProfileUser(HttpServletRequest request, Model model) {
        int userid = getUserFromCookies(request);
        User user = userService.tìmUserTheoId(userid);

        model.addAttribute("user", user);
        model.addAttribute("content", "user/html/profile.html");
        return "layout.html";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_ACCOUNT') || hasRole('ADMIN_CONCERT')")
    @GetMapping("/dashboard")
    public String ProfileUser(Model model) {

        model.addAttribute("content", "admin/pages/dashboard.html");
        return "admin/index.html";
    }

    @GetMapping("/user/history")
    public String HistoryUser(HttpServletRequest request, Model model) {
        int userid = getUserFromCookies(request);
        User user = userService.tìmUserTheoId(userid);
        List<Bill> userBills = billService.findBillsByUserId(userid);
        List<Bill_detail> userBillDetails = new ArrayList<>();
        for (Bill bill : userBills) {
            int billId = bill.getId();
            List<Bill_detail> billDetails = billService.findBillsDetailByBillId(billId);
            userBillDetails.addAll(billDetails);
        }

        model.addAttribute("user", user);
        model.addAttribute("bills", userBills);
        model.addAttribute("billDetails", userBillDetails); // Add bill details to the model
        model.addAttribute("content", "user/html/history.html");

        return "layout.html";
    }

    @GetMapping("/contact_us")
    public String Contact(Model model) {
        model.addAttribute("showHeader", false);
        model.addAttribute("content", "user/html/contact_us.html");
        return "layout.html";
    }

    @GetMapping("/user/changePassword")
    public String ChangePasswordUser(HttpServletRequest request, Model model) {
        int userid = getUserFromCookies(request);
        User user = userService.tìmUserTheoId(userid);

        model.addAttribute("user", user);
        model.addAttribute("content", "user/html/change-password.html");
        return "layout.html";
    }

    @PostMapping("/user/update-profile")
    public String postUpdateProfile(@ModelAttribute("user") User user, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        int userId = getUserFromCookies(request);

        User existingUser = userService.tìmUserTheoId(userId);

        if (existingUser != null) {

            existingUser.setPhoneNo(user.getPhoneNo());
            existingUser.setAddress(user.getAddress());
            existingUser.setGender(user.getGender());
            existingUser.setDob(user.getDob());
            existingUser.setUpdate_at(LocalDate.now());

            userService.updateUser(existingUser);

            redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Cập nhật thành công!");

            return "redirect:/user/profile";
        }

        redirectAttributes.addFlashAttribute("THONG_BAO_LOI", "Không tìm thấy người dùng để cập nhật!");
        return "redirect:/index";
    }

    @PostMapping("/user/changePassword")
    public String handleChangePassword(
            HttpServletRequest request,
            @RequestParam("old_password") String oldPassword,
            @RequestParam("new_password") String newPassword,
            @RequestParam("confirm_password") String confirmPassword,
            Model model) {

        int userId = getUserFromCookies(request);
        User user = userService.tìmUserTheoId(userId);

        if (!userService.checkOldPassword(user, oldPassword)) {
            model.addAttribute("error", "Old password is incorrect.");
            return "user/html/change-password.html";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirm password do not match.");
            return "user/html/change-password.html";
        }

        boolean isPasswordChanged = userService.updatePassword(user, newPassword);

        if (isPasswordChanged) {
            model.addAttribute("success", "Password changed successfully.");
            return "redirect:/user/profile";
        } else {
            model.addAttribute("error", "Failed to change password. Please try again.");
            return "user/html/change-password.html";
        }
    }

    @PostMapping("/user/signup")
    public String postAdd(@ModelAttribute("signUpRequest") User user, HttpServletRequest httpRequest,
            RedirectAttributes redirectAttributes) {

        // Đặt role cho user
        Role role = roleService.findByName("USER");
        user.setCreated_at(LocalDate.now());
        user.setRole_id(role.getId());
        user.setEnabled(false); // Tài khoản chưa được kích hoạt

        // Tạo và gán token xác thực
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userService.saveUser(user);

        // Tạo link xác thực
        String verificationLink = "http://localhost:6868/user/verify?token=" + token;
        try {
            // Gửi email xác thực
            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
            redirectAttributes.addFlashAttribute("success",
                    "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
            return "redirect:/user/login";
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email xác thực: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi gửi email xác thực. Vui lòng thử lại.");
            return "redirect:/user/signup";
        }
    }

    @GetMapping("/user/verify")
    public String verifyUser(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        User user = userService.findByVerificationToken(token);

        if (user == null) {
            log.warn("Invalid verification token");
            redirectAttributes.addFlashAttribute("error", "Invalid verification token.");
            return "redirect:/user/login";
        }

        user.setVerificationToken(null);
        user.setEnabled(true);
        userService.saveUserVerify(user);

        log.info("User verified successfully: {}", user.getEmail());
        redirectAttributes.addFlashAttribute("message", "Account verified successfully!");
        return "redirect:/user/login";
    }

    @PostMapping("/user/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        User user = userService.findUserByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại trong hệ thống.");
            return "redirect:/user/login";
        }

        // Tạo token khôi phục mật khẩu
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userService.saveUserVerify(user);

        // Tạo link reset mật khẩu
        String resetLink = "http://localhost:6868/user/reset-password?token=" + token;
        try {
            // Gửi email xác thực
            emailService.sendResetPassword(user.getEmail(), resetLink);
            redirectAttributes.addFlashAttribute("success",
                    "Hướng dẫn đặt lại mật khẩu đã được gửi tới email của bạn.");
            return "redirect:/user/login";
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email xác thực: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi gửi email xác thực. Vui lòng thử lại.");
            return "redirect:/user/login";
        }
    }

    @GetMapping("/user/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, RedirectAttributes redirectAttributes, Model model) {
        User user = userService.findByVerificationToken(token);
        String email = user.getEmail();

        if (user == null) {
            log.warn("Invalid reset token");
            redirectAttributes.addFlashAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "redirect:/user/login";
        }

        userService.saveUserVerify(user);

        redirectAttributes.addFlashAttribute("message", "Account verified successfully!");

        User userSignUp = new User();
        model.addAttribute("token", token);
        model.addAttribute("email", email);
        model.addAttribute("signUpRequest", userSignUp);
        return "user/html/reset-password";
    }

    @PostMapping("/user/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

            User user = userService.findByVerificationToken(token);
            user.setVerificationToken(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
            return "redirect:/user/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirm password do not match.");
            return "redirect:/user/login";
        }

        boolean isPasswordChanged = userService.updatePassword(user, newPassword);
    
        if (isPasswordChanged) {
            model.addAttribute("success", "Password changed successfully.");
            return "redirect:/user/login";
        } else {
            model.addAttribute("error", "Failed to change password. Please try again.");
            return "redirect:/user/login";
        }
    }

    private int getUserFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equals(cookie.getName())) {
                    String userId = cookie.getValue();

                    return Integer.parseInt(userId);
                }
            }
        }
        return (Integer) null;
    }

}

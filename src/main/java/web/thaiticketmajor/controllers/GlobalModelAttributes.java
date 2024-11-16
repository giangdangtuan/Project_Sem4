package web.thaiticketmajor.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Services.UserService;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.beans.factory.annotation.Autowired;

@ControllerAdvice
@Slf4j
public class GlobalModelAttributes {

    @Autowired
    private UserService userService;

    private Integer getUserFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equals(cookie.getName())) {
                    try {
                        return Integer.parseInt(cookie.getValue());
                    } catch (NumberFormatException e) {
                        // Nếu không thể chuyển đổi, xử lý lỗi tại đây (ví dụ: ghi log hoặc trả về null)
                        return null;
                    }
                }
            }
        }
        return null;
    }

    // Thêm dữ liệu chung vào model cho tất cả các controller
    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest request, Model model) {
        // Thêm dữ liệu chung vào model

        Integer userid = getUserFromCookies(request);
        if (userid != null) {
            User useradvice = userService.tìmUserTheoId(userid);
            if(useradvice != null) {
                model.addAttribute("useradvice", useradvice);
                model.addAttribute("roleadvice", useradvice.getRoleName());
            }
        }
    }
}


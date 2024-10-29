package web.thaiticketmajor.configuration;

import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Models.Role;
import web.thaiticketmajor.Repositories.UserRepository;
import web.thaiticketmajor.Repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
@Slf4j
public class ApplicationInitConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Bean
ApplicationRunner applicationRunner(UserRepository userRepository) {
    return args -> {
        // Kiểm tra nếu người dùng admin chưa tồn tại
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            // Tìm role "SUPER_ADMIN" dựa trên tên
            Optional<Role> superAdminRoleOpt = roleRepository.findByName("SUPER_ADMIN");
            Role superAdminRole = superAdminRoleOpt.orElseThrow(() -> 
                new RuntimeException("Role 'SUPER_ADMIN' not found")
            );

            // Tạo người dùng admin mới
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setPassword(passwordEncoder.encode("admin")); // Mã hóa mật khẩu
 // Gán vai trò cho người dùng
            user.setDob(LocalDate.now()); // Đặt ngày sinh (nếu cần)
            user.setRole_id(1L);
            user.setPhoneNo("099999999"); // Đặt số điện thoại (nếu cần)
            user.setStatus(true); // Đặt trạng thái (nếu cần)

            // Lưu người dùng vào cơ sở dữ liệu
            userRepository.save(user);
            log.warn("Admin user has been created. Please ensure to change the default password.");
        }
    };
}
}

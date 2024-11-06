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
            Optional<Role> superAdminRoleOpt = roleRepository.findByName("SUPER_ADMIN");
            if (superAdminRoleOpt.isEmpty()) {
                Role superAdminRole = new Role();
                superAdminRole.setName("SUPER_ADMIN");
                roleRepository.save(superAdminRole);
                log.info("Role 'SUPER_ADMIN' has been created.");
            }

            Optional<Role> UserRoleOpt = roleRepository.findByName("USER");
            if (UserRoleOpt.isEmpty()) {
                Role userRole = new Role();
                userRole.setName("USER");
                roleRepository.save(userRole);
                log.info("Role 'USER' has been created.");
            }

            // Kiểm tra nếu người dùng admin chưa tồn tại
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                // Tìm role "SUPER_ADMIN" dựa trên tên
                Role superAdminRole = superAdminRoleOpt
                        .orElseThrow(() -> new RuntimeException("Role 'SUPER_ADMIN' not found"));

                // Tạo người dùng admin mới
                User user = new User();
                user.setEmail("admin@gmail.com");
                user.setPassword(passwordEncoder.encode("admin")); // Mã hóa mật khẩu
                // Gán vai trò cho người dùng
                user.setDob(LocalDate.now()); // Đặt ngày sinh (nếu cần)
                user.setCreated_at(LocalDate.now());
                user.setRole_id(superAdminRole.getId());
                user.setPhoneNo("099999999"); // Đặt số điện thoại (nếu cần)
                user.setStatus(true); // Đặt trạng thái (nếu cần)

                // Lưu người dùng vào cơ sở dữ liệu
                userRepository.save(user);
                log.warn("Admin user has been created. Please ensure to change the default password.");
            }
        };
    }
}

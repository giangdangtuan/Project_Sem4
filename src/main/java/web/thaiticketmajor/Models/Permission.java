package web.thaiticketmajor.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id; // Thêm khóa chính tự động tăng
    String name; // Có thể vẫn giữ tên là unique
    String description;

    // @ManyToMany
    // @JoinTable(
    //     name = "role_permissions", // Tạo bảng trung gian
    //     joinColumns = @JoinColumn(name = "permission_nam"), // Khóa ngoại cho Permission
    //     inverseJoinColumns = @JoinColumn(name = "role_nam") // Khóa ngoại cho Role
    // )
    // Set<Role> roles; // Định nghĩa bên sở hữu
}
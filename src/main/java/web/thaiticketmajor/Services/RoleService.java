package web.thaiticketmajor.Services;

import web.thaiticketmajor.Models.Permission;
import web.thaiticketmajor.Models.Role;
import web.thaiticketmajor.Repositories.PermissionRepository;
import web.thaiticketmajor.Repositories.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;


    public Role create(String roleName, String descreption, List<Long> permissionIds) {
        Role role = new Role();
        role.setName(roleName);
        role.setDescription(descreption);
    
        List<Permission> permissionsList = permissionRepository.findAllById(permissionIds);
        role.setPermissions(new HashSet<Permission>(permissionsList));
    
        return roleRepository.save(role);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id); // Sử dụng Optional để tránh NullPointerException
    }

    public Role update(Role role) {
        // Kiểm tra xem role có tồn tại không trước khi cập nhật
        if (!roleRepository.existsById(role.getId())) {
            throw new IllegalArgumentException("Role not found");
        }
        return roleRepository.save(role);
    }

    public void delete(Long role) {
        roleRepository.deleteById(role);

    }
}

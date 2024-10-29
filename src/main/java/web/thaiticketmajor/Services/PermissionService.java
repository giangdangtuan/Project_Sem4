package web.thaiticketmajor.Services;

import web.thaiticketmajor.Models.Permission;
import web.thaiticketmajor.Repositories.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;

    public Permission create(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permissionRepository.save(permission);
    }

    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    public void delete(Long permissionName) {
        permissionRepository.deleteById(permissionName);
    }
}

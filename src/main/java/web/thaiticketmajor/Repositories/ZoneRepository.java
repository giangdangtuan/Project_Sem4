package web.thaiticketmajor.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import web.thaiticketmajor.Models.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {
    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần

    Zone findByName(String name);
}


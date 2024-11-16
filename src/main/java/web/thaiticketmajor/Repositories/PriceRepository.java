package web.thaiticketmajor.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import web.thaiticketmajor.Models.Price;

public interface PriceRepository extends JpaRepository<Price, Integer> {
    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần
}


package web.thaiticketmajor.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import web.thaiticketmajor.Models.Bill;

public interface BillRepository extends JpaRepository<Bill, Integer> {
    List<Bill> findByUserId(int userId);  
}
    

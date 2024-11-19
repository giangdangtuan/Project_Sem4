package web.thaiticketmajor.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import web.thaiticketmajor.Models.Bill_detail;

public interface BillDetailRepository extends JpaRepository<Bill_detail, Integer> {
    List<Bill_detail> findByBillId(int billId);
    }

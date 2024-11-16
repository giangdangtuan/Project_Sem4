package web.thaiticketmajor.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.Bill_detail;
import web.thaiticketmajor.Repositories.BillDetailRepository;

@Service
public class BillDetailService {

    @Autowired
    private BillDetailRepository billDetailRepository;// kho dữ liệu;

    public List<Bill_detail> listBillDetail()
    {

        return billDetailRepository.findAll();
    }

    public Bill_detail findBillById(int id)//
    {

        Bill_detail billDetail = null;

        Optional<Bill_detail> optional = billDetailRepository.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            billDetail = optional.get();
        } else// ngược lại
        {
            // throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung
            // !");
        }

        return billDetail;

    }


    public void save(Bill_detail billDetail) {
        // TODO Auto-generated method stub
        this.billDetailRepository.save(billDetail);
    }

    public void delete(int id) {
        // TODO Auto-generated method stub
        this.billDetailRepository.deleteById(id);
    }
    
}

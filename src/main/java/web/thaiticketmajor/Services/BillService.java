package web.thaiticketmajor.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.Bill;
import web.thaiticketmajor.Repositories.BillRepository;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;// kho dữ liệu;

    public List<Bill> listBill()
    {

        return billRepository.findAll();
    }

    public Bill findBillById(int id)//
    {
        // TODO Auto-generated method stub
        // return null;

        // return kdl.findById(id);

        Bill bill = null;

        Optional<Bill> optional = billRepository.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            bill = optional.get();
        } else// ngược lại
        {
            // throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung
            // !");
        }

        return bill;

    }


    public void save(Bill bill) {
        // TODO Auto-generated method stub
        this.billRepository.save(bill);
    }

    public void delete(int id) {
        // TODO Auto-generated method stub
        this.billRepository.deleteById(id);
    }
}

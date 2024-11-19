package web.thaiticketmajor.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.Contact;
import web.thaiticketmajor.Repositories.ContactRepository;

@Service
public class ContactService {
    
    @Autowired
    private ContactRepository ContactRepository;// kho dữ liệu;

    public List<Contact> listContact() {

        return ContactRepository.findAll();
    }

    public Contact findContactById(int id)//
    {
        // TODO Auto-generated method stub
        // return null;

        // return kdl.findById(id);

        Contact Contact = null;

        Optional<Contact> optional = ContactRepository.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            Contact = optional.get();
        } else// ngược lại
        {
            // throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung
            // !");
        }

        return Contact;

    }

    public void save(Contact Contact) {
        // TODO Auto-generated method stub
        this.ContactRepository.save(Contact);
    }

    public void delete(int id) {
        // TODO Auto-generated method stub
        this.ContactRepository.deleteById(id);
    }

}

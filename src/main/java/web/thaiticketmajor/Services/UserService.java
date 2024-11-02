package web.thaiticketmajor.Services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Repositories.UserRepository;

@Service
public class UserService
{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;// kho dữ liệu;

    public List<User> dsUser() // getAllUser()
    {
  
        // return null;

        // mã bởi lập trình viên:
        return userRepository.findAll();
    }

    public List<User>  duyệtUser() 
    {
        return userRepository.findAll();
    }

    public User  tìmUserTheoId(int id)// 
    {
        // TODO Auto-generated method stub
        // return null;

        // return kdl.findById(id);

        User user = null;

        Optional<User> optional = userRepository.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            user = optional.get();
        } else// ngược lại
        {
            //throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung !");
        }

        return user;

    }

    public User xemUser(int id)// 
    {

        User user = null;

        Optional<User> optional = userRepository.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            user = optional.get();
        } else// ngược lại
        {
            //throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung !");
        }

        return user;

    }

    
    public void lưuUser(User user)
    {
        user.setPassword(passwordEncoder.encode("admin"));
        // TODO Auto-generated method stub
        this.userRepository.save(user);
    }

    public void saveUser(User user)
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        this.userRepository.save(user);
    }

    public void updateUser(User user)
    {

        this.userRepository.save(user);
    }


    
    public void xóaUser(int id)
    {
        // TODO Auto-generated method stub
        this.userRepository.deleteById(id);
    }

}

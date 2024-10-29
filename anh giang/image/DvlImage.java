package web.thaiticketmajor.Models.image;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DvlImage
{
    @Autowired private KdlImage kdl;// kho dữ liệu;

    public List<Image> dsImage() // getAllImage()
    {
  
        // return null;

        // mã bởi lập trình viên:
        return kdl.findAll();
    }

    public List<Image>  duyệtImage() 
    {
        return kdl.findAll();
    }

    public Image  tìmImageTheoId(int id)// 
    {
        // TODO Auto-generated method stub
        // return null;

        // return kdl.findById(id);

        Image dl = null;

        Optional<Image> optional = kdl.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            dl = optional.get();
        } else// ngược lại
        {
            //throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung !");
        }

        return dl;

    }

    public Image xemImage(int id)// 
    {

        Image dl = null;

        Optional<Image> optional = kdl.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            dl = optional.get();
        } else// ngược lại
        {
            //throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung !");
        }

        return dl;

    }

    
    public void lưuImage(Image dl)
    {
        // TODO Auto-generated method stub
        this.kdl.save(dl);
    }

    public void xóaImage(int id)
    {
        // TODO Auto-generated method stub
        this.kdl.deleteById(id);
    }


}


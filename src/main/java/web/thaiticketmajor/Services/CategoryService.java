package web.thaiticketmajor.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.Category;
import web.thaiticketmajor.Repositories.CategoryRepository;

//service
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;// kho dữ liệu;

    public List<Category> dsCategory() // getAllCategory()
    {

        // return null;

        // mã bởi lập trình viên:
        return categoryRepository.findAll();
    }

    public List<Category> duyệtCategory() {
        return categoryRepository.findAll();
    }

    public Category tìmCategoryTheoId(int id)//
    {
        // TODO Auto-generated method stub
        // return null;

        // return kdl.findById(id);

        Category cat = null;

        Optional<Category> optional = categoryRepository.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            cat = optional.get();
        } else// ngược lại
        {
            // throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung
            // !");
        }

        return cat;

    }

    public Category xemCategory(int id)//
    {

        Category cat = null;

        Optional<Category> optional = categoryRepository.findById(id);

        if// nếu
        (optional.isPresent()) // tìm thấy bản ghi trong kho
        {
            cat = optional.get();
        } else// ngược lại
        {
            // throw new RuntimeException("Không tìm thấy thú cưng ! Ko tim thay thu cung
            // !");
        }

        return cat;

    }

    public void lưuCategory(Category cat) {
        // TODO Auto-generated method stub
        this.categoryRepository.save(cat);
    }

    public void xóaCategory(int id) {
        // TODO Auto-generated method stub
        this.categoryRepository.deleteById(id);
    }

}

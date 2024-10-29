package web.thaiticketmajor.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.Category;
import web.thaiticketmajor.Services.CategoryService;

@Controller
@Slf4j
public class CategoryController
{
    @Autowired
    private CategoryService dvl;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    // @ModelAttribute
    // public void addEmailToModel(Model model) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String email = authentication.getName(); // Lấy email của người dùng đăng nhập
    //     model.addAttribute("email", email); // Thêm email vào model
    // }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    // @RolesAllowed("ROLE_ADMIN")
    @GetMapping({
            "/category",
            "/category/duyet"
    })
    public String getDuyet(Model model) 
    {

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String email = authentication.getName();

        // model.addAttribute("email", email);
        List<Category> list = dvl.duyệtCategory();
        Category dl = new Category();
        model.addAttribute("ds", list);
        model.addAttribute("dl", dl);
        model.addAttribute("content", "admin/pages/category-manager.html"); // duyet.html

        return "admin/index.html"; 
    }

    @GetMapping("/category/them")
    public String getThem(Model model) {
        Category dl = new Category();

        // Gửi đối tượng dữ liệu sang bên view
        // để còn ràng buộc vào html form
        model.addAttribute("dl", dl);

        model.addAttribute("action", "/category/them");
        // Nội dung riêng của trang...
        model.addAttribute("content", "admin/pages/category-manager.html"); 
//        model.addAttribute("dsBangNgoai", this.dvlBangNgoai.dsB  angNgoai());

        // ...được đặt vào bố cục chung của toàn website
        return "admin/index.html"; 
    }

    // @GetMapping("/tên_đường_dẫn/sua/{id}")
    @GetMapping("/tên_đường_dẫn/sua")
    public String getSua(Model model, @RequestParam("id") int id) {
        // trangSua(Model model, @PathVariable(value = "id") int id) {
        // Lấy ra bản ghi theo id
        Category dl = dvl.xemCategory(id);

        // Gửi đối tượng dữ liệu sang bên view
        model.addAttribute("dl", dl);
//        model.addAttribute("dsBangNgoai", this.dvlBangNgoai.dsBangNgoai());

        // Hiển thị giao diện view html
        // Nội dung riêng của trang...
        model.addAttribute("content", "tên_đường_dẫn/sua.html"); // sua.html

        // ...được đặt vào bố cục chung của toàn website
        return "layout.html"; // layout.html
    }

    // @GetMapping("/tên_đường_dẫn/xoa/{id}")
    // public String // Giao diện xác nhận xoá
    // trangXoa(Model model, @PathVariable(value = "id") int id) {
    @GetMapping("/category/xoa")
    public String getXoa(Model model, @RequestParam(value = "id") int id) {
        // Lấy ra bản ghi theo id
        Category dl = dvl.tìmCategoryTheoId(id);

        // Gửi đối tượng dữ liệu sang bên view
        model.addAttribute("dl", dl);

        // Hiển thị view giao diện
        // Nội dung riêng của trang...
        model.addAttribute("content", "tên_đường_dẫn/xoa.html"); // xoa.html

        // ...được đặt vào bố cục chung của toàn website
        return "layout.html"; // layout.html
    }

    @GetMapping("/category/xoa/ajax")
    public String getXoaAjax(Model model, @RequestParam("id") int id) 
    {
        // if( Qdl.NhanVienChuaDangNhap(request) )
        //     return "redirect:/qdl/nhanvien/dangnhap";

        // Lấy ra bản ghi theo mã định danh
        var dl = dvl.xemCategory(id);
        model.addAttribute("dl", dl);
        model.addAttribute("action", "/category/xoa");

        return "category/duyet.html"; // layout.html
    }

    @GetMapping("/tên_đường_dẫn/xem/{id}")
    public String getXem(Model model, @PathVariable(value = "id") int id) 
    {
        Category dl = dvl.xemCategory(id);
        model.addAttribute("dl", dl);
        model.addAttribute("content", "tên_đường_dẫn/xem.html"); 
        return "layout.html";    // layout.html
    }

    @PostMapping("/category/them")
    public String postThem(@ModelAttribute("Category") Category dl, RedirectAttributes redirectAttributes) {
        // System.out.print("save action...");
        
        //@todo sửa chỗ này đi
        //dl.setNgayTao(LocalDate.now());
        //dl.setNgaySua(LocalDate.now());
        
        dvl.lưuCategory(dl);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        session.setAttribute("message", "Đã hoàn tất việc thêm mới !");

        return "redirect:/category/duyet";
    }

    // How to send success message to List View
    // https://www.appsloveworld.com/springmvc/100/17/how-to-add-success-notification-after-form-submit
  
    @GetMapping("category/suaajax")
    public String getSuaAjax(Model model, @RequestParam("id") int id) 
    {

        // Lấy ra bản ghi theo mã định danh
        var dl = dvl.xemCategory(id);

        model.addAttribute("dl", dl);
        model.addAttribute("action", "/category/sua");
        return "admin/pages/sua-cat-modal.html"; // layout.html
    }
    @PostMapping("/category/sua")
    public String postSua(@ModelAttribute("Category") Category dl) 
    {
        
        dvl.lưuCategory(dl);
        // dvl.luu(dl);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        session.setAttribute("message", "Đã hoàn tất việc cập nhật !");

        return "redirect:/category/duyet";
    }
    
    // @PostMapping("/category/xoa")
    // public String postXoa(Model model, @RequestParam("id") int id)
    // {
    //     // Xoá dữ liệu
    //     this.dvl.xóaCategory(id);

    //     // Điều hướng sang trang giao diện
    //     return "redirect:/category/duyet";
    // }
    @PostMapping("/category/xoa")
    public String postXoa(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        // Xoá dữ liệu
        this.dvl.xóaCategory(id);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        redirectAttributes.addFlashAttribute("message", "Đã hoàn tất việc xóa !");

        // Điều hướng sang trang giao diện
        return "redirect:/category/duyet";
    }




}// end class

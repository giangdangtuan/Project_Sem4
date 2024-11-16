package web.thaiticketmajor.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import web.thaiticketmajor.Models.Role;
import web.thaiticketmajor.Services.RoleService;

@Controller
public class RoleController {

    @Autowired
    private RoleService roleService; // Cung cấp các dịch vụ thao tác dữ liệu

    @GetMapping("/role")
    public String getRoles(Model model) {
        // Lấy danh sách các quyền
        List<Role> list = roleService.getAll();
        model.addAttribute("ds", list);
        model.addAttribute("content", "admin/pages/role-manager.html"); // Chỉ định trang HTML
        return "admin/index.html"; 
    }

    @GetMapping("/role/them")
    public String getThem(Model model) {
        Role role = new Role();
        model.addAttribute("dl", role);
        return "admin/pages/add-role.html"; // Trang thêm quyền
    }

    @PostMapping("/role/them")
    public String postThem(@ModelAttribute("Role") Role role, RedirectAttributes redirectAttributes) {
        roleService.create(role.getName(), role.getDescription(), List.of()); // Chuyển danh sách permissionIds rỗng
        redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Đã thêm quyền thành công !");
        return "redirect:/role";
    }

    @GetMapping("/role/sua/{id}")
    public String getSua(Model model, @PathVariable("id") Long id) {
        Role role = roleService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));
        model.addAttribute("dl", role);
        model.addAttribute("content", "admin/pages/edit-role.html"); // Trang sửa quyền
        return "admin/index.html"; 
    }

    @PostMapping("/role/sua/{id}")
    public String postSua(@PathVariable("id") String id, @ModelAttribute("Role") Role role, RedirectAttributes redirectAttributes) {
        role.setName(id); // Gán ID cho role
        roleService.update(role); // Cập nhật quyền
        redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Đã sửa quyền thành công !");
        return "redirect:/role";
    }

    @GetMapping("/role/xoa/{id}")
    public String getXoa(Model model, @PathVariable("id") Long id) {
        Role role = roleService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));
        model.addAttribute("dl", role);
        model.addAttribute("content", "admin/pages/delete-role.html"); // Trang xác nhận xóa quyền
        return "admin/index.html"; 
    }

    @PostMapping("/role/xoa/{id}")
    public String postXoa(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        roleService.delete(id);
        redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Đã xóa quyền thành công !");
        return "redirect:/role";
    }
}

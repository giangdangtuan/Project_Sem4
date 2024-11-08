package web.thaiticketmajor.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import web.thaiticketmajor.Models.Bill;
import web.thaiticketmajor.Models.Category;
import web.thaiticketmajor.Services.BillService;

@Controller
public class BillController {
    @Autowired
    private BillService billService;

    @GetMapping({
            "/bill",
            "/bill/list"
    })
    public String getList(Model model) 
    {

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String email = authentication.getName();

        // model.addAttribute("email", email);
        List<Bill> list = billService.listBill();
        model.addAttribute("list", list);
        model.addAttribute("content", "admin/pages/bill-manager.html"); // duyet.html

        return "admin/index.html"; 
    }
}

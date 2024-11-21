package web.thaiticketmajor.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import web.thaiticketmajor.Models.Bill;
import web.thaiticketmajor.Models.Bill_detail;
import web.thaiticketmajor.Models.Concert;
import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Services.BillDetailService;
import web.thaiticketmajor.Services.BillService;
import web.thaiticketmajor.Services.ConcertService;
import web.thaiticketmajor.Services.SeatService;

@Controller
public class BillController {
    @Autowired
    private BillService billService;

    @Autowired
    private BillDetailService billDetailService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ConcertService concertService;
    
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_CONCERT')")
    @GetMapping({
            "/bill",
            "/bill/list"
    })
    public String getList(Model model) {

        List<Bill> list = billService.listBill();
        List<Bill_detail> listDetail = billDetailService.listBillDetail();

        model.addAttribute("listDetail", listDetail);
        model.addAttribute("list", list);
        model.addAttribute("content", "admin/pages/bill-manager.html"); // duyet.html

        return "admin/index.html";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN_CONCERT')")
    @GetMapping("/detail/{id}")
    public String getDetail(@PathVariable int id, Model model) {

        List<Bill_detail> billDetails = billService.findBillsDetailByBillId(id);
        Concert concert = concertService.getConcertById(billService.findBillById(id).getConcert_id());
        String mainImg = concert.getMainImage();
        model.addAttribute("bills", billDetails);
        model.addAttribute("mainImg", mainImg);
        model.addAttribute("content", "admin/pages/billDetails-manager.html"); // duyet.html
        return "admin/index.html";
    }
}

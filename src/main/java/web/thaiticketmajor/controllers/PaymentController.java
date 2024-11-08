package web.thaiticketmajor.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.Bill;
import web.thaiticketmajor.Models.Bill_detail;
import web.thaiticketmajor.Models.Concert;
import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Services.BillDetailService;
import web.thaiticketmajor.Services.BillService;
import web.thaiticketmajor.Services.EmailService;
import web.thaiticketmajor.Services.SeatService;
import web.thaiticketmajor.Services.UserService;
import web.thaiticketmajor.Services.VNPayService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.stereotype.Controller
@Slf4j
public class PaymentController {
    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private BillDetailService billDetailService;

    @Autowired
    private BillService billService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") String orderTotal,
            @RequestParam("orderInfo") String orderInfo,
            HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
public String GetMapping(HttpServletRequest request, Model model) {
    int paymentStatus = vnPayService.orderReturn(request);

    String orderInfo = request.getParameter("vnp_OrderInfo");
    String paymentTime = request.getParameter("vnp_PayDate");
    String transactionId = request.getParameter("vnp_TransactionNo");
    String totalPrice = request.getParameter("vnp_Amount");

    model.addAttribute("orderId", orderInfo);
    model.addAttribute("totalPrice", totalPrice);
    model.addAttribute("paymentTime", paymentTime);
    model.addAttribute("transactionId", transactionId);

    if (paymentStatus == 1) {
        // Lấy selectedSeatIds từ session
        List<Integer> selectedSeatIds = (List<Integer>) request.getSession().getAttribute("selectedSeatIds");

        if (selectedSeatIds != null && !selectedSeatIds.isEmpty()) {
            // Tạo đối tượng Bill mới
            Bill bill = new Bill();
            List<Bill_detail> listBillDetail = new ArrayList<>();
            bill.setTotal(Double.parseDouble(totalPrice));  // Cập nhật tổng số tiền thanh toán (totalPrice)

            // Giả sử có một phương thức để lấy người dùng từ session
            int userid = getUserFromCookies(request);
            User user = userService.tìmUserTheoId(userid);
            bill.setUser_id(userid); // Gán người dùng vào Bill
            bill.setUser(user);
            // Lưu Bill vào cơ sở dữ liệu
            billService.save(bill);

            // Lưu Bill_detail cho mỗi ghế đã chọn
            for (Integer seatId : selectedSeatIds) {
                Seat seat = seatService.findById(seatId);
                if (seat != null) {
                    // Tạo đối tượng Bill_detail
                    Bill_detail billDetail = new Bill_detail();
                    billDetail.setBill_id(bill.getId());  // Gán Bill vào Bill_detail
                    billDetail.setBill(bill);
                    billDetail.setSeat_id(seatId);  // Gán Seat vào Bill_detail
                    billDetail.setSeat(seat); 

                    // Lưu Bill_detail vào cơ sở dữ liệu
                    billDetailService.save(billDetail);
                    listBillDetail.add(billDetail);

                    // Cập nhật trạng thái ghế là đã thanh toán
                    seat.setBooked(true); // Cập nhật trạng thái ghế
                    seatService.save(seat); // Lưu ghế vào cơ sở dữ liệu
                }
            }
            emailService.sendEmail(
                "giangbe291@gmail.com", 
                "Welcome to Our Service", 
                bill, 
                listBillDetail
            );

            // Xóa session sau khi lưu để tránh trùng lặp
            request.getSession().removeAttribute("selectedSeatIds");

            return "user/html/success.html"; // Đặt return ở cuối sau khi xử lý xong
        }
    }

    return "user/html/fail.html"; // Nếu thanh toán không thành công
}

    private int getUserFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equals(cookie.getName())) {
                    String userId = cookie.getValue();
                    // Tìm người dùng theo userId từ cơ sở dữ liệu
                    return Integer.parseInt(userId); // Giả sử bạn có phương thức findById trong userService
                }
            }
        }
        return (Integer) null; // Trả về null nếu không tìm thấy người dùng từ cookie
    }

}

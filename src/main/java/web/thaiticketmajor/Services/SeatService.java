package web.thaiticketmajor.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Repositories.SeatRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getAllSeats() {
        return seatRepository.findAll(); // Trả về danh sách tất cả ghế
    }

    public void reserveSeats(List<Integer> seatIds) {
        for (int seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Ghế không tồn tại"));
            seat.setStatus(true); // Đặt trạng thái ghế thành true (đã đặt)
            seatRepository.save(seat); // Lưu lại vào cơ sở dữ liệu
        }
    }

    public void addSeats(int rows, int columns) {
        List<Seat> newSeats = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Seat seat = new Seat();
                seat.setRow(String.valueOf(i)); // Số hàng
                seat.setSeat_column(String.valueOf(j)); // Số cột
                seat.setSeat_row(i); // Chỉ định lại
                seat.setStatus(false); // Đặt trạng thái ban đầu là chưa đặt
                seat.setZone_id(1);;
                seat.setConcert_id(1);
                newSeats.add(seat);
            }
        }
        seatRepository.saveAll(newSeats); // Lưu tất cả ghế mới vào cơ sở dữ liệu
    }
}


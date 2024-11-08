package web.thaiticketmajor.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import web.thaiticketmajor.Models.Concert;
import web.thaiticketmajor.Models.ConcertZone;
import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Models.Zone;
import web.thaiticketmajor.Repositories.ConcertRepository;
import web.thaiticketmajor.Repositories.SeatRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConcertService {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private ConcertZoneService concertZoneService;

    // Thêm Concert và Seats
    // public Concert addConcertAndSeats(Concert concert, int rows, int columns) {
    // // Lưu concert trước
    // Concert savedConcert = concertRepository.save(concert);

    // // Tạo danh sách ghế và liên kết concert_id
    // List<Seat> seats = new ArrayList<>();
    // for (int i = 1; i <= rows; i++) {
    // for (int j = 1; j <= columns; j++) {
    // Seat seat = new Seat();
    // seat.setRow(String.valueOf(i));
    // seat.setSeat_column(String.valueOf(j));
    // seat.setSeat_row(i);
    // seat.setStatus(false);
    // seat.setConcertZone_id(1);
    // seat.setZone_id(1); // Đặt ID Zone nếu có
    // seat.setConcert_id(savedConcert.getId()); // Gán concert_id từ savedConcert
    // seats.add(seat);
    // }
    // }

    // // Lưu tất cả ghế
    // seatRepository.saveAll(seats);
    // return savedConcert; // Trả về concert đã lưu
    // }
    public Concert addConcertAndSeats(Concert concert, int rows, int columns) {
        // Lưu concert trước

        // Tìm ID của ConcertZone cho zone "standard"
        Integer standardZoneId = null;
        List<ConcertZone> concertZones = concertZoneService.getConcertZonesByConcertId(concert.getId());

        for (ConcertZone cz : concertZones) {
            Zone zone = zoneService.getZoneById(cz.getZone_id());
            if ("standard".equals(zone.getName())) {
                standardZoneId = cz.getId(); // Lưu ID của ConcertZone cho zone "standard"
                break;
            }
        }

        // Tạo danh sách ghế và liên kết concert_id
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Seat seat = new Seat();
                seat.setRow(String.valueOf(i));
                seat.setSeat_column(String.valueOf(j));
                seat.setSeat_row(i);
                seat.setZone_id(1);
                seat.setStatus(true);
                seat.setConcertZone_id(standardZoneId); // Đặt ID ConcertZone là ID của zone "standard"
                seat.setConcert_id(concert.getId());
                seat.setBooked(false); // Gán concert_id từ savedConcert
                seats.add(seat);
            }
        }

        // Lưu tất cả ghế
        seatRepository.saveAll(seats);
        return concert; // Trả về concert đã lưu
    }

    public Concert getConcertById(int id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert không tồn tại"));
    }

    public List<Seat> getSeatsByConcertId(int concertId) {
        return seatRepository.findByConcertId(concertId);
    }

    public List<Concert> dsConcert() // getAllUser()
    {

        // return null;

        // mã bởi lập trình viên:
        return concertRepository.findAll();
    }

    public void updateConcert(Concert concert) {
        this.concertRepository.save(concert); // Lưu concert đã được cập nhật
    }

    @Transactional
    public void deleteConcertAndSeats(int id) {
        // Xóa tất cả ghế liên quan đến concert trước khi xóa concert
        seatRepository.deleteByConcertId(id);

        // Xóa concert
        concertRepository.deleteById(id);
    }
}

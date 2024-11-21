package web.thaiticketmajor.Services;

import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
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

    public Concert addConcertAndSeats(Concert concert, int rows, int columns) {

        Integer standardZoneId = null;
        List<ConcertZone> concertZones = concertZoneService.getConcertZonesByConcertId(concert.getId());

        for (ConcertZone cz : concertZones) {
            Zone zone = zoneService.getZoneById(cz.getZone_id());
            if ("standard".equals(zone.getName())) {
                standardZoneId = cz.getId();
                break;
            }
        }

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Seat seat = new Seat();
                char columnLetter = (char) ('A' + j-1);
                seat.setSeat_column(String.valueOf(columnLetter));
                seat.setSeat_row(i);
                seat.setStatus(true);
                seat.setConcertZone_id(standardZoneId); // Đặt ID ConcertZone là ID của zone "standard"
                seat.setConcert_id(concert.getId());
                seat.setBooked(false); // Gán concert_id từ savedConcert
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
        return concert;
    }

    public Concert getConcertById(int id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert không tồn tại"));
    }

    public List<Concert> getConcertByCategoryId(int id) {
        return concertRepository.findByCategory_id(id);
    }

    public List<Seat> getSeatsByConcertId(int concertId) {
        return seatRepository.findByConcertId(concertId);
    }

    public List<Concert> dsConcert() {

        return concertRepository.findAll();
    }

    public void updateConcert(Concert concert) {
        this.concertRepository.save(concert);
    }

    @Transactional
    public void deleteConcertAndSeats(int id) {

        seatRepository.deleteByConcertId(id);

        concertRepository.deleteById(id);
    }

   
}

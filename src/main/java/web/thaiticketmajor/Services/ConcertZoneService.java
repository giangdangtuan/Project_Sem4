package web.thaiticketmajor.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.Concert;
import web.thaiticketmajor.Models.ConcertZone;
import web.thaiticketmajor.Repositories.ConcertZoneRepository;

import java.util.List;

@Service
public class ConcertZoneService {
    @Autowired
    private ConcertZoneRepository concertZoneRepository;

    public ConcertZone save(ConcertZone concertZone) {
        return concertZoneRepository.save(concertZone);
    }

    public List<ConcertZone> getConcertZonesByConcertId(int concertId) {
        return concertZoneRepository.findByConcertId(concertId);
    }

    

    public void saveConcertZone(int concertId, int zoneId, double price) {
        ConcertZone concertZone = concertZoneRepository.findByConcertIdAndZoneId(concertId, zoneId);
        if (concertZone == null) {
            // Nếu không tìm thấy, tạo mới
            concertZone = new ConcertZone();
            concertZone.setConcert_id(concertId);
            concertZone.setZone_id(zoneId);
        }
        concertZone.setPrice(price);
        concertZoneRepository.save(concertZone); // Lưu vào cơ sở dữ liệu
    }

    public void deleteConcertZone(int concertZoneId) {
        concertZoneRepository.deleteById(concertZoneId);
    }
    
    
}

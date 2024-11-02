package web.thaiticketmajor.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.thaiticketmajor.Models.Category;
import web.thaiticketmajor.Models.Zone;
import web.thaiticketmajor.Repositories.ZoneRepository;

import java.util.List;

@Service
public class ZoneService {
    @Autowired
    private ZoneRepository zoneRepository;

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(int id) {
        return zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone không tồn tại"));
    }

    public Zone findByName(String name) {
        // Sử dụng ZoneRepository để tìm zone theo tên
        return zoneRepository.findByName(name);
    }

    public Integer getZoneIdByName(String name) {
        Zone zone = zoneRepository.findByName(name); // Cần có phương thức này trong ZoneRepository
        return zone != null ? zone.getId() : null;
    }

    public void saveZone(Zone zone) {
        // TODO Auto-generated method stub
        this.zoneRepository.save(zone);
    }

    public void deleteZone(int id) {
        this.zoneRepository.deleteById(id);
    }

}

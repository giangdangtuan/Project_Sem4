package web.thaiticketmajor.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}

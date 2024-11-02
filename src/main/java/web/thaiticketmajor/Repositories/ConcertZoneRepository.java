package web.thaiticketmajor.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.thaiticketmajor.Models.ConcertZone;
import java.util.List;

@Repository
public interface ConcertZoneRepository extends JpaRepository<ConcertZone, Integer> {

    List<ConcertZone> findByConcertId(int concertId);

    ConcertZone findByConcertIdAndZoneId(int concertId, int zoneId);

    

}

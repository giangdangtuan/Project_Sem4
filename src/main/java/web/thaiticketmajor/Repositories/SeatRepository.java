package web.thaiticketmajor.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import web.thaiticketmajor.Models.Seat;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByConcertId(int concertId);

    List<Seat> findByConcertZoneId(int concertZoneId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Seat s WHERE s.concert.id = :concertId")
    void deleteByConcertId(@Param("concertId") int concertId);
}

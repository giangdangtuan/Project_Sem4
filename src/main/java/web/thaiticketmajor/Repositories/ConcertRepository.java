package web.thaiticketmajor.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import web.thaiticketmajor.Models.Concert;

public interface ConcertRepository extends JpaRepository<Concert, Integer>
{

}

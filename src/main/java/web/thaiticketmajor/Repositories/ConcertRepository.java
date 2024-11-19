package web.thaiticketmajor.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import web.thaiticketmajor.Models.Concert;

public interface ConcertRepository extends JpaRepository<Concert, Integer>
{
    List<Concert> findByCategory_id(int category_id);

}

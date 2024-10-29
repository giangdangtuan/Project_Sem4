package web.thaiticketmajor.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import web.thaiticketmajor.Models.Image;

public interface ImageRepository extends JpaRepository<Image, Integer>
{
   
}

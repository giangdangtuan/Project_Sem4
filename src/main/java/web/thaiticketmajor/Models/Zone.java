package web.thaiticketmajor.Models;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Zone
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private    int id;
    private String name;

    private String color; 

    @OneToMany(mappedBy = "zone")
    private List<ConcertZone> concertZones = new ArrayList<>();
}
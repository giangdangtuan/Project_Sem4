package web.thaiticketmajor.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ConcertZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int concert_id;
    @ManyToOne
    @JoinColumn(name = "concert_id", insertable=false, updatable=false)
    private Concert concert;

    private int zone_id;
    @ManyToOne
    @JoinColumn(name = "zone_id", insertable=false, updatable=false)
    private Zone zone;

    private double price;
}

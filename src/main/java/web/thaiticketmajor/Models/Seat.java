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
public class Seat
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private    int id;
    private String row; 
    private String seat_column;
    private int seat_row; 
    private boolean status;

    private int concert_id;
    @ManyToOne
    @JoinColumn(name = "concert_id",insertable=false,updatable=false)
    private Concert concert;

    private int zone_id;
    @ManyToOne
    @JoinColumn(name = "zone_id",insertable=false,updatable=false)
    private Zone zone;

    private int concertZone_id;

    @ManyToOne
    @JoinColumn(name = "concertZone_id",insertable=false,updatable=false)
    private ConcertZone concertZone;

    private boolean booked;

    public String getColor() {
        return this.concertZone.getZone().getColor();
    }
    
    public int getConcertZone() {
        return this.concertZone_id;
    }

    public String getZone() {
        return this.concertZone.getZoneName();
    }

    public double getPrice() {
        return this.concertZone.getPrice();
    }

    public String getConcert() {
        return this.concert.getName();
    }
}
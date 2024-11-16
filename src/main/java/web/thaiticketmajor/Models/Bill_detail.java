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
public class Bill_detail
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private    int id;

    private int bill_id;
    @ManyToOne @JoinColumn(name="bill_id",insertable=false,updatable=false)
    private Bill bill;

    private int seat_id;
    @ManyToOne @JoinColumn(name="seat_id",insertable=false,updatable=false)
    private Seat seat;

    public String getConcert(){
        return this.seat.getConcert();
    }

    public String getZone() {
        return this.seat.getZone();
    }

    public double getPrice() {
        return this.seat.getPrice();
    }
}
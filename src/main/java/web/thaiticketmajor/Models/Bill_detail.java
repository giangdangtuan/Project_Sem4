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
    @ManyToOne @JoinColumn(name="bill_id",insertable=false,updatable=false)
    private Bill Bill;
    @ManyToOne @JoinColumn(name="ticket_id",insertable=false,updatable=false)
    private Ticket Ticket;
}
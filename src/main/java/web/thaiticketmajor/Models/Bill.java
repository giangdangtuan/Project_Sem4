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
public class Bill
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;

    @ManyToOne @JoinColumn(name="user_id",insertable=false,updatable=false)
    private User user;

    @ManyToOne @JoinColumn(name="detail_id",insertable=false,updatable=false)
    private Concert Concert;

    private int total;
    private boolean status;
}  
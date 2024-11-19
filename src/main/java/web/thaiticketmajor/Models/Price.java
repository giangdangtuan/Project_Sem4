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
public class Price
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private    int id;

    private int id_concert;
    @ManyToOne @JoinColumn(name="con_id",insertable=false,updatable=false)
    private Concert Concert;

    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone Zone;

}
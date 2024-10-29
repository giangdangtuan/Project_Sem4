package web.thaiticketmajor.Models;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
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
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int Duration;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startSale;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endSale;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    private String videoLink;
    private boolean status;
    private String banner;
    @Column(columnDefinition = "LONGTEXT")
    private String moTa;
    public int Popularity;

    @Column(name = "main_image")
    private String mainImage;

    @Column(name = "sub_image_1")
    private String subImage1;

    @Column(name = "sub_image_2")
    private String subImage2;

    private int category_id;
    @ManyToOne
    @JoinColumn(name = "category_id", insertable=false, updatable=false)
    private Category category;
}
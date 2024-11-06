package web.thaiticketmajor.Models;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category
{
    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tăng tự động từ 1,2,3,...
    private int id;
    private String catname ;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate created_at;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate update_at;
}
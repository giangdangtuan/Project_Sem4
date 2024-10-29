package web.thaiticketmajor.Models;



import java.time.LocalDate;
import java.util.Date;


import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    Long id;
    String email; 
    @Column(columnDefinition = "LONGTEXT")
    String password; 

    @DateTimeFormat(pattern = "yyyy-MM-dd") // Định dạng phù hợp với input type="date"
    LocalDate dob;
    String phoneNo;
    boolean status;

    Long role_id;
    @ManyToOne @JoinColumn(name="role_id",insertable=false,updatable=false)
    Role role; // Đảm bảo Role có trường "name" tương ứng
}

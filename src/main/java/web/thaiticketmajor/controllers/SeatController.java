package web.thaiticketmajor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Services.SeatService;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/")
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats(); // Gọi phương thức getAllSeats
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveSeats(@RequestBody List<Integer> seatIds) {
        seatService.reserveSeats(seatIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addSeats(@RequestParam int rows, @RequestParam int columns) {
        seatService.addSeats(rows, columns);
        return ResponseEntity.ok().build();
    }
}


package web.thaiticketmajor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


import lombok.extern.slf4j.Slf4j;
import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Repositories.SeatRepository;
import web.thaiticketmajor.Services.SeatService;
import web.thaiticketmajor.dto.request.ChangeZoneRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

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

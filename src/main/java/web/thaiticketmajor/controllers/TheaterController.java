package web.thaiticketmajor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web.thaiticketmajor.Models.Seat;
import web.thaiticketmajor.Models.Zone;
import web.thaiticketmajor.Services.SeatService;
import web.thaiticketmajor.Services.ZoneService;

import java.util.List;

@Controller
public class TheaterController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private ZoneService zoneService;

    @GetMapping("/theater")
    public String showTheater(Model model) {
        List<Seat> seats = seatService.getAllSeats();
        List<Zone> zones = zoneService.getAllZones();
        
        model.addAttribute("seats", seats);
        model.addAttribute("zones", zones);
        
        return "seat.html"; // trả về tệp theater.html
    }
}

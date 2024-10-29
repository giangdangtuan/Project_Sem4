// package web.thaiticketmajor.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import web.thaiticketmajor.Models.Zone;
// import web.thaiticketmajor.Services.ZoneService;

// import java.util.List;

// @Controller
// public class ZoneController {

//     @Autowired
//     private ZoneService zoneService;

//     @GetMapping("/zones")
//     public String getAllZones(Model model) {
//         List<Zone> zones = zoneService.getAllZones();
//         model.addAttribute("zones", zones);
//         return "zones"; // Trả về tên file template
//     }

//     @GetMapping("/zones/{id}")
//     public String getZoneById(@PathVariable Long id, Model model) {
//         Zone zone = zoneService.getZoneById(id);
//         model.addAttribute("zone", zone);
//         return "zone_detail"; // Trả về tên file template chi tiết
//     }
// }

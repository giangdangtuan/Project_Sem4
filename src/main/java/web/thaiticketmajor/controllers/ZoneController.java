package web.thaiticketmajor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import web.thaiticketmajor.Models.Category;
import web.thaiticketmajor.Models.User;
import web.thaiticketmajor.Models.Zone;
import web.thaiticketmajor.Services.ZoneService;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @GetMapping("/zones")
    public String getAllZones(Model model) {
        List<Zone> listZone = zoneService.getAllZones();
        Zone zone = new Zone();

        model.addAttribute("listZone", listZone);
        model.addAttribute("zone", zone);
        model.addAttribute("content", "admin/pages/zone-manager.html"); // duyet.html

        return "admin/index.html"; 
    }

    @GetMapping("/zone/update")
    public String getSua(Model model, @RequestParam("id") int id) {
        var zone = zoneService.getZoneById(id);

        model.addAttribute("zone", zone);
        model.addAttribute("action", "/zone/update");

        return "admin/pages/update-zone.html";
    }

    @GetMapping("/zones/{id}")
    public String getZoneById(@PathVariable int id, Model model) {
        Zone zone = zoneService.getZoneById(id);
        model.addAttribute("zone", zone);
        return "zone_detail"; // Trả về tên file template chi tiết
    }

    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @PostMapping("/zone/add")
    public String postThem(@ModelAttribute("Zone") Zone zone, RedirectAttributes redirectAttributes) {

        
        zoneService.saveZone(zone);

        return "redirect:/zones";
    }

    @PostMapping("/zone/update")
    public String postSua(@ModelAttribute("Zone") Zone zone, RedirectAttributes redirectAttributes) {
        
        zoneService.saveZone(zone);

        // Gửi thông báo thành công từ view Add/Edit sang view List
        redirectAttributes.addFlashAttribute("THONG_BAO_OK", "Đã sửa thành công !");

        return "redirect:/zones";
    }

    @PostMapping("/zone/delete")
    public String postXoa(Model model, @RequestParam("Id") int id) // request param phải khớp với name="Id" của thẻ html input
    {

        this.zoneService.deleteZone(id);
        return "redirect:/zones";
    }
    
}

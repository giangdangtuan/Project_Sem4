package web.thaiticketmajor.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import web.thaiticketmajor.Models.Contact;
import web.thaiticketmajor.Repositories.ContactRepository;


@Controller
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactRepository contactMessageRepository;

    @PostMapping("/submit")
    public String submitForm(@ModelAttribute Contact contactMessage,Model model) {
        contactMessageRepository.save(contactMessage);
        model.addAttribute("mess", "Success !!!");
        return "redirect:/contact_us";  // Redirect sau khi lưu thành công
    }
}

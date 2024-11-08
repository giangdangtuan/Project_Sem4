package web.thaiticketmajor.Services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import web.thaiticketmajor.Models.Bill;
import web.thaiticketmajor.Models.Bill_detail;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;  // Autowired Thymeleaf TemplateEngine

    // Send email with HTML content generated from a template
    public void sendEmail(String to, String subject, Bill bill, List<Bill_detail> listBillDetail) {
        try {
            // Load HTML template
            String htmlTemplate = loadTemplate("welcome-email.html");
    
            // Prepare the context (variables for Thymeleaf)
            Context context = new Context();
            context.setVariable("bill", bill);
            context.setVariable("listBillDetail", listBillDetail);
    
            // Process the template with the context (inject values into the HTML content)
            String htmlContent = templateEngine.process("welcome-email", context);
    
            // Create MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);  // 'true' for multi-part (HTML)
    
            // Set email details
            messageHelper.setFrom("thaitqth2210015@fpt.edu.vn");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(htmlContent, true);  // 'true' indicates HTML content
    
            // Send the email
            javaMailSender.send(mimeMessage);
            System.out.println("HTML email sent successfully.");
        } catch (Exception e) {
            System.out.println("Error sending HTML email: " + e.getMessage());
        }
    }
    

    // Helper method to load the template from resources
    private String loadTemplate(String templateName) throws Exception {
        // Load the template file from classpath
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    }
}
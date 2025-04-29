package org.example.tulparelectric.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send-template-email")
    public ResponseEntity<String> sendTemplateEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("erasylsagintaev03@gmail.com");
            message.setSubject("Заявка");
            message.setText("Поступила новая заявка через блок.");

            mailSender.send(message);

            return ResponseEntity.ok("Письмо отправлено");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }
}

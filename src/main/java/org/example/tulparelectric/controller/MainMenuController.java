package org.example.tulparelectric.controller;

import jakarta.servlet.ServletContext;
import org.example.tulparelectric.model.ElectricianCatalog;
import org.example.tulparelectric.service.ClickFraudDetectorService;
import org.example.tulparelectric.service.ElectricianService;
import org.example.tulparelectric.service.EmailService;
import org.example.tulparelectric.utils.IpExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

@Controller
public class MainMenuController {

    private final static Logger log = LoggerFactory.getLogger(MainMenuController.class);
    private final ElectricianService electricianService;
    private final EmailService emailService;
    private ClickFraudDetectorService fraudDetector;
    private final ServletContext servletContext;

    @Value("${upload.path}")
    private String uploadDir;

    public MainMenuController(ElectricianService electricianService, EmailService emailService, ClickFraudDetectorService fraudDetector, ServletContext servletContext) {
        this.electricianService = electricianService;
        this.emailService = emailService;
        this.fraudDetector = fraudDetector;
        this.servletContext = servletContext;
    }


    @GetMapping("/")
    public String getHome(HttpServletRequest request, Model model) {
        Collections.list(request.getHeaderNames()).forEach(header ->
                log.debug("Header {}: {}", header, request.getHeader(header))
        );

        String ip = IpExtractor.getClientIp(request);
        log.debug("Extracted IP: {}", ip);

        if (fraudDetector.checkForFraud(ip)) {
            log.warn("Потенциальный кликфрод с IP: {}", ip);
        }

        model.addAttribute("services", electricianService.findAll());
        return "homeMenu";
    }

    @GetMapping("/admin-panel")
    public String adminPanel(Model model) {
        return "adminPanel";
    }

    @GetMapping("/services/{id}")
    public String viewServiceDetail(@PathVariable Long id, Model model) {
        ElectricianCatalog service = electricianService.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));
        model.addAttribute("service", service);
        return "service-detail";
    }

    @GetMapping("/admin-panel/create")
    public String showCreateForm(Model model) {
        model.addAttribute("service", new ElectricianCatalog());
        return "create-service";
    }

    @PostMapping("/admin-panel/create")
    public String createService(@ModelAttribute("service") ElectricianCatalog service,
                                @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        String folderName = StringUtils.cleanPath(service.getServiceName().replaceAll("\\s+", "_"));
        Path uploadPath = Paths.get(uploadDir, folderName);
        Files.createDirectories(uploadPath);

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, imageFile.getBytes());

        // Измените URL путь
        service.setServiceImage("/uploads/" + folderName + "/" + fileName);

        electricianService.save(service);
        return "redirect:/admin-panel";
    }

    @GetMapping("/admin-panel/update")
    public String showUpdateList(Model model) {
        model.addAttribute("services", electricianService.findAll());
        return "update-list";
    }

    @GetMapping("/admin-panel/edit/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        var service = electricianService.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));
        model.addAttribute("service", service);
        return "edit-service";
    }

    @PostMapping("/admin-panel/edit/{id}")
    public String updateService(@PathVariable Long id,
                                @ModelAttribute("service") ElectricianCatalog updatedService,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String folderName = updatedService.getServiceName().replaceAll("\\s+", "_");
            Path uploadPath = Paths.get(uploadDir, folderName);
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageFile.getBytes());

            updatedService.setServiceImage("/uploads/" + folderName + "/" + fileName);
        }

        electricianService.update(id, updatedService);
        return "redirect:/admin-panel";
    }

    @GetMapping("/admin-panel/delete")
    public String showDeleteList(Model model) {
        model.addAttribute("services", electricianService.findAll());
        return "delete-list";
    }

    @GetMapping("/admin-panel/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        electricianService.deleteById(id);
        return "redirect:/admin-panel/delete";
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestParam String fullName,
                            @RequestParam String body,
                            Model model) {
        try {
            String to = "erasylsagintaev03@gmail.com";
            String subject = "Новая заявка от клиента";
            String content = "<b>Имя клиента:</b> " + fullName + "<br><b>Описание услуги:</b><br>" + body;

            emailService.sendEmail(to, subject, content);
            model.addAttribute("message", "Заявка успешно отправлена!");
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при отправке письма: " + e.getMessage());
        }
        model.addAttribute("services", electricianService.findAll());
        return "homeMenu";
    }
}

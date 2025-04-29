package org.example.tulparelectric.service;

import org.example.tulparelectric.utils.IpExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClickFraudDetectorService {
    private final static Logger log = LoggerFactory.getLogger(ClickFraudDetectorService.class);
    private final EmailService emailService;
    private final Map<String, LinkedList<Instant>> ipClickTimestamps = new ConcurrentHashMap<>();

    // Email получателя уведомлений о кликфроде
    private static final String FRAUD_ALERT_EMAIL = "tulparelectrictech@gmail.com";

    @Autowired
    public ClickFraudDetectorService(EmailService emailService) {
        this.emailService = emailService;
    }

    public synchronized boolean checkForFraud(String ip) {
        if (ip == null || ip.isEmpty()) {
            log.warn("Unable to determine client IP");
            return false;
        }

        // 1. Получение и обновление истории кликов
        LinkedList<Instant> clicks = ipClickTimestamps.computeIfAbsent(ip, k -> new LinkedList<>());
        clicks.add(Instant.now());

        // 2. Удаление старых записей (>5 минут)
        clicks.removeIf(time -> time.isBefore(Instant.now().minus(5, ChronoUnit.MINUTES)));

        // 3. Проверка на кликфрод (>=3 клика за 5 минут)
        if (clicks.size() >= 3) {
            log.info("[CLICK-FRAUD DETECTED] Suspicious IP: {} ({} clicks in 5 minutes)", ip, clicks.size());
            sendFraudAlertEmail(ip, clicks.size());
            return true;
        }

        return false;
    }

    private void sendFraudAlertEmail(String ip, int clickCount) {
        String subject = "⚠️ Обнаружен кликфрод";
        String body = String.format(
                "<h3>Обнаружена подозрительная активность</h3>" +
                        "<p><b>IP-адрес:</b> %s</p>" +
                        "<p><b>Количество кликов:</b> %d за 5 минут</p>" +
                        "<p><b>Время обнаружения:</b> %s</p>" +
                        "<p>Рекомендуется проверить активность с этого IP.</p>",
                ip, clickCount, LocalDateTime.now()
        );

        try {
            emailService.sendEmail(FRAUD_ALERT_EMAIL, subject, body);
            log.info("Уведомление о кликфроде отправлено на {}", FRAUD_ALERT_EMAIL);
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления о кликфроде", e);
        }
    }
}
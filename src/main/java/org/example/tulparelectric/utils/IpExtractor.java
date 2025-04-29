package org.example.tulparelectric.utils;
import jakarta.servlet.http.HttpServletRequest;


public class IpExtractor {
    public static String getClientIp(HttpServletRequest request) {
        // Проверяем заголовки в правильном порядке
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_REAL_IP",
                "HTTP_CLIENT_IP"
        };

        String ip = null;
        for (String header : headers) {
            ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }

        // Если IP не найден в заголовках, берем remote address
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Обрабатываем случаи с несколькими IP (цепочка прокси)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // Локальные адреса (для тестирования)
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "127.0.0.1"; // нормализуем для единообразия
            // Можно добавить логирование, что обнаружен локальный запрос
        }

        return ip;
    }
}
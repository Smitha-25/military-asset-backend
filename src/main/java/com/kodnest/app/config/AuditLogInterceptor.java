
package com.kodnest.app.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kodnest.app.entities.AuditLog;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.AuditLogRepository;
import com.kodnest.app.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuditLogInterceptor implements HandlerInterceptor {

    private static final Logger logger =
            LoggerFactory.getLogger(AuditLogInterceptor.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogInterceptor(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        String uri = request.getRequestURI();

        // Log only API requests, excluding audit-log endpoints.
        if (!uri.startsWith("/api/")
                || uri.startsWith("/api/audit-logs")) {
            return;
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElse(null);

            if (user == null) {
                return;
            }

            String action = request.getMethod();
            String details = "Endpoint: " + uri
                    + ", Status: " + response.getStatus();

            AuditLog auditLog = new AuditLog(
                    user,
                    action,
                    details,
                    LocalDateTime.now()
            );

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            logger.error("Failed to save API audit log for {}", uri, e);
        }
    }
}
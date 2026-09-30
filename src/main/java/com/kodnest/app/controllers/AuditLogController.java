
package com.kodnest.app.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.kodnest.app.entities.AuditLog;
import com.kodnest.app.services.AuditLogService;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<AuditLog> getAllAuditLogs() {
        return auditLogService.getAllAuditLogs();
    }

    @GetMapping("/{id}")
    public AuditLog getAuditLogById(@PathVariable Long id) {
        return auditLogService.getAuditLogById(id);
    }
}
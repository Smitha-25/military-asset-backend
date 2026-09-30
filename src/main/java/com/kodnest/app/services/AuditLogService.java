
package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.AuditLog;

public interface AuditLogService {

    AuditLog saveAuditLog(AuditLog auditLog);

    List<AuditLog> getAllAuditLogs();

    AuditLog getAuditLogById(Long id);
}
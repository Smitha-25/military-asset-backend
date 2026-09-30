package com.kodnest.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kodnest.app.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
package com.kodnest.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kodnest.app.entities.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
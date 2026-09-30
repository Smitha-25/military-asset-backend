package com.kodnest.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kodnest.app.entities.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
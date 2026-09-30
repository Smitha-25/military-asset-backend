
package com.kodnest.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kodnest.app.entities.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
	
}
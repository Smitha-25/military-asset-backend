package com.kodnest.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kodnest.app.entities.Base;

public interface BaseRepository extends JpaRepository<Base, Long> {
	
}
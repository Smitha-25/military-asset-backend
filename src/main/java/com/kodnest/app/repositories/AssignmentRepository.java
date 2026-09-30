
package com.kodnest.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kodnest.app.entities.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByBaseId(Long baseId);
}
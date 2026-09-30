
package com.kodnest.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kodnest.app.entities.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findByBaseId(Long baseId);
}
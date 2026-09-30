
package com.kodnest.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.kodnest.app.entities.AssetStock;

public interface AssetStockRepository
        extends JpaRepository<AssetStock, Long> {

    Optional<AssetStock> findByBase_IdAndEquipment_Id(
            Long baseId, Long equipmentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AssetStock s " +
           "WHERE s.base.id = :baseId " +
           "AND s.equipment.id = :equipmentId")
    Optional<AssetStock> findByBaseAndEquipmentForUpdate(
            @Param("baseId") Long baseId,
            @Param("equipmentId") Long equipmentId);
}

package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.Equipment;

public interface EquipmentService {

    Equipment saveEquipment(Equipment equipment);

    List<Equipment> getAllEquipment();

    Equipment getEquipmentById(Long id);

    Equipment updateEquipment(Long id, Equipment equipment);

    void deleteEquipment(Long id);
}
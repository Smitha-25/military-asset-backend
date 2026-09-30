
package com.kodnest.app.serviceimplementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kodnest.app.entities.Equipment;
import com.kodnest.app.repositories.EquipmentRepository;
import com.kodnest.app.services.EquipmentService;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public Equipment saveEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Override
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    @Override
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    @Override
    public Equipment updateEquipment(Long id, Equipment equipment) {
        Equipment existing = getEquipmentById(id);
        existing.setName(equipment.getName());
        existing.setType(equipment.getType());
        existing.setDescription(equipment.getDescription());
        return equipmentRepository.save(existing);
    }

    @Override
    public void deleteEquipment(Long id) {
        Equipment existing = getEquipmentById(id);
        equipmentRepository.delete(existing);
    }
}
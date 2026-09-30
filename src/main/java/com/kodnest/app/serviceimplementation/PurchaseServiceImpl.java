package com.kodnest.app.serviceimplementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.app.entities.AssetStock;
import com.kodnest.app.entities.Purchase;
import com.kodnest.app.repositories.AssetStockRepository;
import com.kodnest.app.repositories.BaseRepository;
import com.kodnest.app.repositories.EquipmentRepository;
import com.kodnest.app.repositories.PurchaseRepository;
import com.kodnest.app.services.PurchaseService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final AssetStockRepository stockRepository;
    private final BaseRepository baseRepository;
    private final EquipmentRepository equipmentRepository;

    public PurchaseServiceImpl(
            PurchaseRepository purchaseRepository,
            AssetStockRepository stockRepository,
            BaseRepository baseRepository,
            EquipmentRepository equipmentRepository) {
        this.purchaseRepository = purchaseRepository;
        this.stockRepository = stockRepository;
        this.baseRepository = baseRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    @Transactional
    public Purchase savePurchase(Purchase purchase) {

        if (purchase.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (purchase.getBase() == null
                || purchase.getBase().getId() == null
                || purchase.getEquipment() == null
                || purchase.getEquipment().getId() == null) {
            throw new IllegalArgumentException(
                    "Base and equipment are required");
        }

        Long baseId = purchase.getBase().getId();
        Long equipmentId = purchase.getEquipment().getId();

        if (!baseRepository.existsById(baseId)) {
            throw new EntityNotFoundException(
                    "Base not found with id: " + baseId);
        }

        if (!equipmentRepository.existsById(equipmentId)) {
            throw new EntityNotFoundException(
                    "Equipment not found with id: " + equipmentId);
        }

        AssetStock stock = stockRepository
                .findByBaseAndEquipmentForUpdate(
                        baseId, equipmentId)
                .orElseGet(() -> {
                    AssetStock newStock = new AssetStock();
                    newStock.setBase(purchase.getBase());
                    newStock.setEquipment(purchase.getEquipment());
                    newStock.setQuantity(0);
                    return newStock;
                });

        stock.setQuantity(
                stock.getQuantity() + purchase.getQuantity());

        stockRepository.save(stock);

        return purchaseRepository.save(purchase);
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() ->
                new EntityNotFoundException(
                        "Purchase not found with id: " + id));
    }

    @Override
    @Transactional
    public void deletePurchase(Long id) {
        throw new UnsupportedOperationException(
                "Purchase deletion is disabled to preserve stock history");
    }
}
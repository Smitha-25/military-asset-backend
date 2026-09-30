
package com.kodnest.app.serviceimplementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.app.entities.AssetStock;
import com.kodnest.app.entities.Transfer;
import com.kodnest.app.repositories.AssetStockRepository;
import com.kodnest.app.repositories.BaseRepository;
import com.kodnest.app.repositories.TransferRepository;
import com.kodnest.app.services.TransferService;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AssetStockRepository assetStockRepository;
    private final BaseRepository baseRepository;

    public TransferServiceImpl(
            TransferRepository transferRepository,
            AssetStockRepository assetStockRepository,
            BaseRepository baseRepository) {
        this.transferRepository = transferRepository;
        this.assetStockRepository = assetStockRepository;
        this.baseRepository = baseRepository;
    }

    @Override
    @Transactional
    public Transfer saveTransfer(Transfer transfer) {

        if (transfer.getQuantity() <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Quantity must be greater than zero"
            );
        }

        if (transfer.getFromBase() == null ||
            transfer.getToBase() == null ||
            transfer.getEquipment() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Base and equipment are required"
            );
        }

        Long fromBaseId = transfer.getFromBase().getId();
        Long toBaseId = transfer.getToBase().getId();
        Long equipmentId = transfer.getEquipment().getId();

        if (fromBaseId == null || toBaseId == null ||
            equipmentId == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid base or equipment ID"
            );
        }

        if (fromBaseId.equals(toBaseId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Source and destination bases must be different"
            );
        }

        // Validate that both bases exist.
        if (!baseRepository.existsById(fromBaseId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Source base not found"
            );
        }

        if (!baseRepository.existsById(toBaseId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Destination base not found"
            );
        }

        // Lock stock records in a consistent base-ID order.
        Long firstBaseId = Math.min(fromBaseId, toBaseId);
        Long secondBaseId = Math.max(fromBaseId, toBaseId);

        AssetStock firstStock = assetStockRepository
                .findByBaseAndEquipmentForUpdate(firstBaseId, equipmentId)
                .orElse(null);

        AssetStock secondStock = assetStockRepository
                .findByBaseAndEquipmentForUpdate(secondBaseId, equipmentId)
                .orElse(null);

        AssetStock sourceStock = fromBaseId.equals(firstBaseId)
                ? firstStock : secondStock;

        AssetStock destinationStock = toBaseId.equals(firstBaseId)
                ? firstStock : secondStock;

        if (sourceStock == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Source stock not found"
            );
        }

        if (sourceStock.getQuantity() < transfer.getQuantity()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Insufficient stock at source base"
            );
        }

        // Deduct stock from source.
        sourceStock.setQuantity(
                sourceStock.getQuantity() - transfer.getQuantity());

        // Add stock to destination.
        if (destinationStock == null) {
            destinationStock = new AssetStock();
            destinationStock.setBase(transfer.getToBase());
            destinationStock.setEquipment(transfer.getEquipment());
            destinationStock.setQuantity(0);
        }

        destinationStock.setQuantity(
                destinationStock.getQuantity() + transfer.getQuantity());

        assetStockRepository.save(sourceStock);
        assetStockRepository.save(destinationStock);

        if (transfer.getTransferDate() == null) {
            transfer.setTransferDate(LocalDateTime.now());
        }

        return transferRepository.save(transfer);
    }

    @Override
    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    @Override
    public Transfer getTransferById(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Transfer not found"));
    }

    @Override
    public void deleteTransfer(Long id) {
        throw new UnsupportedOperationException(
                "Transfer deletion is disabled to preserve stock history");
    }
}
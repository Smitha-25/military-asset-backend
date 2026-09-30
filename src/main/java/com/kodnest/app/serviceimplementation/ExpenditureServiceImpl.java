
package com.kodnest.app.serviceimplementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kodnest.app.entities.AssetStock;
import com.kodnest.app.entities.Expenditure;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.AssetStockRepository;
import com.kodnest.app.repositories.ExpenditureRepository;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.services.ExpenditureService;

@Service
public class ExpenditureServiceImpl implements ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final AssetStockRepository assetStockRepository;
    private final UserRepository userRepository;

    public ExpenditureServiceImpl(
            ExpenditureRepository expenditureRepository,
            AssetStockRepository assetStockRepository,
            UserRepository userRepository) {
        this.expenditureRepository = expenditureRepository;
        this.assetStockRepository = assetStockRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void checkBaseAccess(Long baseId) {
        User user = getLoggedInUser();

        if ("BASE_COMMANDER".equalsIgnoreCase(user.getRole())) {
            if (user.getBase() == null ||
                    !user.getBase().getId().equals(baseId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You can access only your assigned base");
            }
        }
    }

    @Override
    @Transactional
    public Expenditure saveExpenditure(Expenditure expenditure) {

        if (expenditure.getQuantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than zero");
        }

        if (expenditure.getBase() == null ||
                expenditure.getEquipment() == null ||
                expenditure.getBase().getId() == null ||
                expenditure.getEquipment().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid base and equipment are required");
        }

        Long baseId = expenditure.getBase().getId();
        Long equipmentId = expenditure.getEquipment().getId();

        // Restrict Base Commander to their assigned base.
        checkBaseAccess(baseId);

        // Lock the stock record before updating it.
        AssetStock stock = assetStockRepository
                .findByBaseAndEquipmentForUpdate(baseId, equipmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Stock not found for the selected base and equipment"));

        // Check available stock.
        if (stock.getQuantity() < expenditure.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient stock for this expenditure");
        }

        // Deduct the expended quantity.
        stock.setQuantity(
                stock.getQuantity() - expenditure.getQuantity());

        assetStockRepository.save(stock);

        // Set expenditure date if not provided.
        if (expenditure.getExpenditureDate() == null) {
            expenditure.setExpenditureDate(LocalDateTime.now());
        }

        return expenditureRepository.save(expenditure);
    }

    @Override
    public List<Expenditure> getAllExpenditures() {
        User user = getLoggedInUser();

        if ("BASE_COMMANDER".equalsIgnoreCase(user.getRole())) {
            if (user.getBase() == null) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No base is assigned to this user");
            }

            return expenditureRepository.findByBaseId(
                    user.getBase().getId());
        }

        return expenditureRepository.findAll();
    }

    @Override
    public Expenditure getExpenditureById(Long id) {
        Expenditure expenditure = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expenditure not found"));

        checkBaseAccess(expenditure.getBase().getId());

        return expenditure;
    }

    @Override
    public void deleteExpenditure(Long id) {
        throw new UnsupportedOperationException(
                "Expenditure deletion is disabled to preserve stock history");
    }
}
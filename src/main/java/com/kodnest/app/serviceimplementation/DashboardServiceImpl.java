
package com.kodnest.app.serviceimplementation;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kodnest.app.dto.DashboardResponse;
import com.kodnest.app.entities.Equipment;
import com.kodnest.app.entities.Purchase;
import com.kodnest.app.entities.Transfer;
import com.kodnest.app.entities.Assignment;
import com.kodnest.app.entities.Expenditure;
import com.kodnest.app.entities.AssetStock;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.AssetStockRepository;
import com.kodnest.app.repositories.PurchaseRepository;
import com.kodnest.app.repositories.TransferRepository;
import com.kodnest.app.repositories.AssignmentRepository;
import com.kodnest.app.repositories.ExpenditureRepository;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.services.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final AssetStockRepository assetStockRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(
            AssetStockRepository assetStockRepository,
            PurchaseRepository purchaseRepository,
            TransferRepository transferRepository,
            AssignmentRepository assignmentRepository,
            ExpenditureRepository expenditureRepository,
            UserRepository userRepository) {

        this.assetStockRepository = assetStockRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(
            LocalDate startDate,
            LocalDate endDate,
            Long baseId,
            String equipmentType) {

        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date cannot be after end date");
        }

        // Restrict Base Commander to their assigned base.
        User user = getLoggedInUser();

        if ("BASE_COMMANDER".equalsIgnoreCase(user.getRole())) {
            if (user.getBase() == null) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No base is assigned to this user");
            }

            Long assignedBaseId = user.getBase().getId();

            if (baseId != null && !baseId.equals(assignedBaseId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You can access only your assigned base");
            }

            baseId = assignedBaseId;
        }

        // Final variable for use inside stream lambdas.
        final Long selectedBaseId = baseId;

        DashboardResponse response = new DashboardResponse();

        // Current stock for the selected base and equipment type.
        long closing = assetStockRepository.findAll().stream()
                .filter(s -> matchesBase(
                        s.getBase().getId(), selectedBaseId))
                .filter(s -> matchesType(
                        s.getEquipment(), equipmentType))
                .mapToLong(AssetStock::getQuantity)
                .sum();

        // Reverse movements after the selected end date.
        if (endDate != null) {

            long purchasesAfter = purchaseRepository.findAll().stream()
                    .filter(p -> matchesBase(
                            p.getBase().getId(), selectedBaseId))
                    .filter(p -> matchesType(
                            p.getEquipment(), equipmentType))
                    .filter(p -> p.getPurchaseDate().isAfter(endDate))
                    .mapToLong(Purchase::getQuantity)
                    .sum();

            long transferAfter = transferRepository.findAll().stream()
                    .filter(t -> matchesType(
                            t.getEquipment(), equipmentType))
                    .filter(t -> t.getTransferDate()
                            .toLocalDate().isAfter(endDate))
                    .mapToLong(t -> {
                        long quantity = t.getQuantity();
                        long reverse = 0;

                        if (matchesBase(
                                t.getFromBase().getId(), selectedBaseId)) {
                            reverse += quantity;
                        }

                        if (matchesBase(
                                t.getToBase().getId(), selectedBaseId)) {
                            reverse -= quantity;
                        }

                        return reverse;
                    })
                    .sum();

            long assignmentsAfter =
                    assignmentRepository.findAll().stream()
                            .filter(a -> matchesBase(
                                    a.getBase().getId(), selectedBaseId))
                            .filter(a -> matchesType(
                                    a.getEquipment(), equipmentType))
                            .filter(a -> a.getAssignmentDate()
                                    .toLocalDate().isAfter(endDate))
                            .mapToLong(Assignment::getQuantity)
                            .sum();

            long expendituresAfter =
                    expenditureRepository.findAll().stream()
                            .filter(e -> matchesBase(
                                    e.getBase().getId(), selectedBaseId))
                            .filter(e -> matchesType(
                                    e.getEquipment(), equipmentType))
                            .filter(e -> e.getExpenditureDate()
                                    .toLocalDate().isAfter(endDate))
                            .mapToLong(Expenditure::getQuantity)
                            .sum();

            closing = closing - purchasesAfter
                    - transferAfter
                    + assignmentsAfter
                    + expendituresAfter;
        }

        // Movements during the selected period.
        long purchases = purchaseRepository.findAll().stream()
                .filter(p -> matchesBase(
                        p.getBase().getId(), selectedBaseId))
                .filter(p -> matchesType(
                        p.getEquipment(), equipmentType))
                .filter(p -> inRange(
                        p.getPurchaseDate(), startDate, endDate))
                .mapToLong(Purchase::getQuantity)
                .sum();

        long transferIn = transferRepository.findAll().stream()
                .filter(t -> matchesBase(
                        t.getToBase().getId(), selectedBaseId))
                .filter(t -> matchesType(
                        t.getEquipment(), equipmentType))
                .filter(t -> inRange(
                        t.getTransferDate().toLocalDate(),
                        startDate, endDate))
                .mapToLong(Transfer::getQuantity)
                .sum();

        long transferOut = transferRepository.findAll().stream()
                .filter(t -> matchesBase(
                        t.getFromBase().getId(), selectedBaseId))
                .filter(t -> matchesType(
                        t.getEquipment(), equipmentType))
                .filter(t -> inRange(
                        t.getTransferDate().toLocalDate(),
                        startDate, endDate))
                .mapToLong(Transfer::getQuantity)
                .sum();

        long assigned = assignmentRepository.findAll().stream()
                .filter(a -> matchesBase(
                        a.getBase().getId(), selectedBaseId))
                .filter(a -> matchesType(
                        a.getEquipment(), equipmentType))
                .filter(a -> inRange(
                        a.getAssignmentDate().toLocalDate(),
                        startDate, endDate))
                .mapToLong(Assignment::getQuantity)
                .sum();

        long expended = expenditureRepository.findAll().stream()
                .filter(e -> matchesBase(
                        e.getBase().getId(), selectedBaseId))
                .filter(e -> matchesType(
                        e.getEquipment(), equipmentType))
                .filter(e -> inRange(
                        e.getExpenditureDate().toLocalDate(),
                        startDate, endDate))
                .mapToLong(Expenditure::getQuantity)
                .sum();

        long netMovement = purchases + transferIn - transferOut;

        long opening = closing - netMovement
                + assigned + expended;

        response.setOpeningBalance(opening);
        response.setClosingBalance(closing);
        response.setNetMovement(netMovement);
        response.setAssigned(assigned);
        response.setExpended(expended);

        return response;
    }

    private boolean matchesBase(Long recordBaseId, Long baseId) {
        return baseId == null || baseId.equals(recordBaseId);
    }

    private boolean matchesType(
            Equipment equipment, String equipmentType) {
        return equipmentType == null
                || equipmentType.isBlank()
                || equipmentType.equalsIgnoreCase(equipment.getType());
    }

    private boolean inRange(
            LocalDate date, LocalDate startDate, LocalDate endDate) {
        return date != null
                && (startDate == null || !date.isBefore(startDate))
                && (endDate == null || !date.isAfter(endDate));
    }
}

package com.kodnest.app.serviceimplementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.app.entities.AssetStock;
import com.kodnest.app.entities.Assignment;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.AssetStockRepository;
import com.kodnest.app.repositories.AssignmentRepository;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.services.AssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssetStockRepository assetStockRepository;
    private final UserRepository userRepository;

    public AssignmentServiceImpl(
            AssignmentRepository assignmentRepository,
            AssetStockRepository assetStockRepository,
            UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.assetStockRepository = assetStockRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public Assignment saveAssignment(Assignment assignment) {

        if (assignment.getQuantity() <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        if (assignment.getBase() == null ||
            assignment.getEquipment() == null ||
            assignment.getBase().getId() == null ||
            assignment.getEquipment().getId() == null) {
            throw new RuntimeException(
                    "Valid base and equipment are required");
        }

        User user = getLoggedInUser();

        if ("BASE_COMMANDER".equalsIgnoreCase(user.getRole())) {
            if (user.getBase() == null ||
                !user.getBase().getId().equals(
                        assignment.getBase().getId())) {
            	throw new ResponseStatusException(
            		    HttpStatus.FORBIDDEN,
            		    "You can only assign equipment at your base");
            }
        }

        Long baseId = assignment.getBase().getId();
        Long equipmentId = assignment.getEquipment().getId();

        AssetStock stock = assetStockRepository
                .findByBaseAndEquipmentForUpdate(baseId, equipmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Stock not found for the selected base and equipment"));

        if (stock.getQuantity() < assignment.getQuantity()) {
            throw new RuntimeException(
                    "Insufficient stock for this assignment");
        }

        stock.setQuantity(
                stock.getQuantity() - assignment.getQuantity());

        assetStockRepository.save(stock);

        if (assignment.getAssignmentDate() == null) {
            assignment.setAssignmentDate(LocalDateTime.now());
        }

        return assignmentRepository.save(assignment);
    }

    @Override
    public List<Assignment> getAllAssignments() {

        User user = getLoggedInUser();

        if ("BASE_COMMANDER".equalsIgnoreCase(user.getRole())) {
            if (user.getBase() == null) {
            	throw new ResponseStatusException(
            		    HttpStatus.FORBIDDEN,
            		    "No base is assigned to this commander");
            }

            return assignmentRepository.findByBaseId(
                    user.getBase().getId());
        }

        return assignmentRepository.findAll();
    }


@Override
public Assignment getAssignmentById(Long id) {

    Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Assignment not found"));

    User user = getLoggedInUser();

    if ("BASE_COMMANDER".equalsIgnoreCase(user.getRole())) {

        if (user.getBase() == null ||
            !user.getBase().getId().equals(
                    assignment.getBase().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access assignments from another base");
        }
    }

    return assignment;
}

    @Override
    public void deleteAssignment(Long id) {
        throw new UnsupportedOperationException(
                "Assignment deletion is disabled to preserve stock history");
    }
}

package com.kodnest.app.serviceimplementation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kodnest.app.entities.AssetStock;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.AssetStockRepository;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.services.AssetStockService;

@Service
public class AssetStockServiceImpl implements AssetStockService {

    private final AssetStockRepository stockRepository;
    private final UserRepository userRepository;

    public AssetStockServiceImpl(
            AssetStockRepository stockRepository,
            UserRepository userRepository) {
        this.stockRepository = stockRepository;
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
    public AssetStock saveStock(AssetStock stock) {
        if (stock.getQuantity() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity cannot be negative");
        }

        if (stock.getBase() == null || stock.getBase().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Valid base is required");
        }

        checkBaseAccess(stock.getBase().getId());

        return stockRepository.save(stock);
    }

    @Override
    public List<AssetStock> getAllStock() {
        User user = getLoggedInUser();

        if ("BASE_COMMANDER".equalsIgnoreCase(user.getRole())) {
            if (user.getBase() == null) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No base is assigned to this user");
            }

            return getStockByBase(user.getBase().getId());
        }

        return stockRepository.findAll();
    }

    @Override
    public AssetStock getStockById(Long id) {
        AssetStock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stock not found"));

        checkBaseAccess(stock.getBase().getId());

        return stock;
    }

    @Override
    public List<AssetStock> getStockByBase(Long baseId) {
        checkBaseAccess(baseId);

        return stockRepository.findAll().stream()
                .filter(stock -> stock.getBase() != null
                        && stock.getBase().getId().equals(baseId))
                .toList();
    }

    @Override
    public void deleteStock(Long id) {
        AssetStock stock = getStockById(id);
        stockRepository.delete(stock);
    }
}
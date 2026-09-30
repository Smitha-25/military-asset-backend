
package com.kodnest.app.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.kodnest.app.entities.AssetStock;
import com.kodnest.app.services.AssetStockService;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "http://localhost:5173")
public class AssetStockController {

    private final AssetStockService assetStockService;

    public AssetStockController(AssetStockService assetStockService) {
        this.assetStockService = assetStockService;
    }

    @PostMapping
    public AssetStock saveStock(@RequestBody AssetStock stock) {
        return assetStockService.saveStock(stock);
    }

    @GetMapping
    public List<AssetStock> getAllStock() {
        return assetStockService.getAllStock();
    }

    @GetMapping("/{id}")
    public AssetStock getStockById(@PathVariable Long id) {
        return assetStockService.getStockById(id);
    }

    @GetMapping("/base/{baseId}")
    public List<AssetStock> getStockByBase(
            @PathVariable Long baseId) {
        return assetStockService.getStockByBase(baseId);
    }

    @DeleteMapping("/{id}")
    public String deleteStock(@PathVariable Long id) {
        assetStockService.deleteStock(id);
        return "Stock deleted successfully";
    }
}
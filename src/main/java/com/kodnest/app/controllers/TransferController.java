
package com.kodnest.app.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.kodnest.app.entities.Transfer;
import com.kodnest.app.services.TransferService;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "http://localhost:5173")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public Transfer saveTransfer(@RequestBody Transfer transfer) {
        return transferService.saveTransfer(transfer);
    }

    @GetMapping
    public List<Transfer> getAllTransfers() {
        return transferService.getAllTransfers();
    }

    @GetMapping("/{id}")
    public Transfer getTransferById(@PathVariable Long id) {
        return transferService.getTransferById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return "Transfer deleted successfully";
    }
}
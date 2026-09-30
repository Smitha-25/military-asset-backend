
package com.kodnest.app.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.kodnest.app.entities.Expenditure;
import com.kodnest.app.services.ExpenditureService;

@RestController
@RequestMapping("/api/expenditures")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @PostMapping
    public Expenditure saveExpenditure(
            @RequestBody Expenditure expenditure) {
        return expenditureService.saveExpenditure(expenditure);
    }

    @GetMapping
    public List<Expenditure> getAllExpenditures() {
        return expenditureService.getAllExpenditures();
    }

    @GetMapping("/{id}")
    public Expenditure getExpenditureById(@PathVariable Long id) {
        return expenditureService.getExpenditureById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteExpenditure(@PathVariable Long id) {
        expenditureService.deleteExpenditure(id);
        return "Expenditure deleted successfully";
    }
}

package com.kodnest.app.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.kodnest.app.entities.Base;
import com.kodnest.app.services.BaseService;

@RestController
@RequestMapping("/api/bases")
@CrossOrigin(origins = "http://localhost:5173")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @PostMapping
    public Base saveBase(@RequestBody Base base) {
        return baseService.saveBase(base);
    }

    @GetMapping
    public List<Base> getAllBases() {
        return baseService.getAllBases();
    }

    @GetMapping("/{id}")
    public Base getBaseById(@PathVariable Long id) {
        return baseService.getBaseById(id);
    }

    @PutMapping("/{id}")
    public Base updateBase(
            @PathVariable Long id,
            @RequestBody Base base) {
        return baseService.updateBase(id, base);
    }

    @DeleteMapping("/{id}")
    public String deleteBase(@PathVariable Long id) {
        baseService.deleteBase(id);
        return "Base deleted successfully";
    }
}
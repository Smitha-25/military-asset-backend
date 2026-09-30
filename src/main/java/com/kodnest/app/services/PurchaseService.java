
package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.Purchase;

public interface PurchaseService {

    Purchase savePurchase(Purchase purchase);

    List<Purchase> getAllPurchases();

    Purchase getPurchaseById(Long id);

    void deletePurchase(Long id);
}
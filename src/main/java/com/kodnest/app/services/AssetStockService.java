
package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.AssetStock;

public interface AssetStockService {

    AssetStock saveStock(AssetStock stock);

    List<AssetStock> getAllStock();

    AssetStock getStockById(Long id);

    List<AssetStock> getStockByBase(Long baseId);

    void deleteStock(Long id);
}
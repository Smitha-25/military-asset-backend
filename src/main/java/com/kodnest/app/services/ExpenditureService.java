
package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.Expenditure;

public interface ExpenditureService {

    Expenditure saveExpenditure(Expenditure expenditure);

    List<Expenditure> getAllExpenditures();

    Expenditure getExpenditureById(Long id);

    void deleteExpenditure(Long id);
}

package com.kodnest.app.services;

import java.time.LocalDate;
import com.kodnest.app.dto.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(
            LocalDate startDate,
            LocalDate endDate,
            Long baseId,
            String equipmentType);
}
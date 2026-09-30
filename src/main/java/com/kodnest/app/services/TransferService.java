
package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.Transfer;

public interface TransferService {

    Transfer saveTransfer(Transfer transfer);

    List<Transfer> getAllTransfers();

    Transfer getTransferById(Long id);

    void deleteTransfer(Long id);
}
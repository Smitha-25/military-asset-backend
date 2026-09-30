package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.Base;

public interface BaseService {

    Base saveBase(Base base);

    List<Base> getAllBases();

    Base getBaseById(Long id);

    Base updateBase(Long id, Base base);

    void deleteBase(Long id);
}
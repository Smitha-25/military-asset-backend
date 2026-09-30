package com.kodnest.app.serviceimplementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kodnest.app.entities.Base;
import com.kodnest.app.repositories.BaseRepository;
import com.kodnest.app.services.BaseService;

@Service
public class BaseServiceImpl implements BaseService {

    private final BaseRepository baseRepository;

    public BaseServiceImpl(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    public Base saveBase(Base base) {
        return baseRepository.save(base);
    }

    @Override
    public List<Base> getAllBases() {
        return baseRepository.findAll();
    }

    @Override
    public Base getBaseById(Long id) {
        return baseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Base not found"));
    }

    @Override
    public Base updateBase(Long id, Base base) {
        Base existingBase = getBaseById(id);
        existingBase.setName(base.getName());
        existingBase.setLocation(base.getLocation());
        return baseRepository.save(existingBase);
    }

    @Override
    public void deleteBase(Long id) {
        Base existingBase = getBaseById(id);
        baseRepository.delete(existingBase);
    }
}
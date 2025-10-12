package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AdminStoreService {

    private final StoreRepository storeRepository;

    public AdminStoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public List<Store> list() {
        return storeRepository.findAll();
    }

    @Transactional
    public Store create(String code, String name, String address, BigDecimal lat, BigDecimal lng, String phone,
            String openHours, Boolean active) {
        Store s = new Store();
        s.setCode(code);
        s.setName(name);
        s.setAddress(address);
        s.setLat(lat);
        s.setLng(lng);
        s.setPhone(phone);
        s.setOpenHours(openHours);
        if (active != null)
            s.setActive(active);
        return storeRepository.save(s);
    }

    @Transactional
    public Store update(UUID id, String name, String address, BigDecimal lat, BigDecimal lng, String phone,
            String openHours, Boolean active) {
        Store s = storeRepository.findById(id).orElseThrow();
        if (name != null)
            s.setName(name);
        if (address != null)
            s.setAddress(address);
        if (lat != null)
            s.setLat(lat);
        if (lng != null)
            s.setLng(lng);
        if (phone != null)
            s.setPhone(phone);
        if (openHours != null)
            s.setOpenHours(openHours);
        if (active != null)
            s.setActive(active);
        return storeRepository.save(s);
    }

    @Transactional
    public void delete(UUID id) {
        storeRepository.deleteById(id);
    }
}

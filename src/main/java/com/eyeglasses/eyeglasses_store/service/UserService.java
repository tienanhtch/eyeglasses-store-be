package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.user.Address;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.user.AddressRepository;
import com.eyeglasses.eyeglasses_store.repository.user.SavedFilterRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final AddressRepository addressRepository;
    private final SavedFilterRepository savedFilterRepository;

    public UserService(AppUserRepository userRepository, AddressRepository addressRepository,
            SavedFilterRepository savedFilterRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.savedFilterRepository = savedFilterRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProfile(UUID userId) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Map<String, Object> m = new HashMap<>();
        m.put("id", user.getId());
        m.put("email", user.getEmail());
        m.put("fullName", user.getFullName());
        m.put("phone", user.getPhone());
        m.put("isActive", user.isActive());
        m.put("roles", user.getRoles().stream().map(r -> r.getCode()).toList());
        m.put("createdAt", user.getCreatedAt());
        m.put("updatedAt", user.getUpdatedAt());
        return m;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listAddresses(UUID userId) {
        return addressRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toAddressPayload).toList();
    }

    @Transactional
    public Map<String, Object> createAddress(UUID userId, Map<String, Object> body) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Address address = new Address();
        address.setUser(user);
        address.setRecipient((String) body.get("recipient"));
        address.setPhone((String) body.get("phone"));
        address.setLine1((String) body.get("line1"));
        address.setLine2((String) body.get("line2"));
        address.setCity((String) body.get("city"));
        address.setDistrict((String) body.get("district"));
        address.setWard((String) body.get("ward"));
        address.setPostalCode((String) body.get("postalCode"));
        if (body.get("country") != null)
            address.setCountry((String) body.get("country"));
        if (body.get("isDefault") != null)
            address.setDefaultAddress(Boolean.TRUE.equals(body.get("isDefault")));
        Address saved = addressRepository.save(address);
        return toAddressPayload(saved);
    }

    private Map<String, Object> toAddressPayload(Address a) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", a.getId());
        m.put("recipient", a.getRecipient());
        m.put("phone", a.getPhone());
        m.put("line1", a.getLine1());
        m.put("line2", a.getLine2());
        m.put("city", a.getCity());
        m.put("district", a.getDistrict());
        m.put("ward", a.getWard());
        m.put("postalCode", a.getPostalCode());
        m.put("country", a.getCountry());
        m.put("isDefault", a.isDefaultAddress());
        m.put("createdAt", a.getCreatedAt());
        return m;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listSavedFilters(UUID userId) {
        return savedFilterRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(sf -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", sf.getId());
            m.put("name", sf.getName());
            m.put("paramsJson", sf.getParamsJson());
            m.put("createdAt", sf.getCreatedAt());
            return m;
        }).toList();
    }

    @Transactional
    public Map<String, Object> saveFilter(UUID userId, String name, String paramsJson) {
        com.eyeglasses.eyeglasses_store.entity.user.AppUser user = userRepository.findById(userId).orElseThrow();
        com.eyeglasses.eyeglasses_store.entity.user.SavedFilter sf = new com.eyeglasses.eyeglasses_store.entity.user.SavedFilter();
        sf.setUser(user);
        sf.setName(name);
        sf.setParamsJson(paramsJson);
        var saved = savedFilterRepository.save(sf);
        return java.util.Map.of("id", saved.getId());
    }
}

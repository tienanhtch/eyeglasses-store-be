package com.eyeglasses.eyeglasses_store.repository.lens;

import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LensPackageRepository extends JpaRepository<LensPackage, UUID> {
}

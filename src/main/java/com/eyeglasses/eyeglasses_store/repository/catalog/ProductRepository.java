package com.eyeglasses.eyeglasses_store.repository.catalog;

import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

        @EntityGraph(attributePaths = { "categories" })
        List<Product> findAllByPublishedTrue();

        @EntityGraph(attributePaths = { "categories" })
        Optional<Product> findBySlug(String slug);

        @Query("select distinct p from Product p left join p.categories c " +
                        " left join com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant v on v.product = p " +
                        " where p.published = true " +
                        " and (:q is null or lower(p.name) like lower(concat('%', :q, '%')) or lower(p.description) like lower(concat('%', :q, '%'))) "
                        +
                        " and (:categorySlug is null or c.slug = :categorySlug) " +
                        " and (:material is null or lower(p.material) like lower(concat('%', :material, '%'))) " +
                        " and (:frameShape is null or lower(p.frameShape) = lower(:frameShape)) " +
                        " and (:isNew is null or p.isNew = :isNew) " +
                        " and (:isBestSeller is null or p.isBestSeller = :isBestSeller) " +
                        " and (:minPrice is null or (v.retailPrice is not null and v.retailPrice >= :minPrice)) " +
                        " and (:maxPrice is null or (v.retailPrice is not null and v.retailPrice <= :maxPrice))")
        Page<Product> search(
                        @Param("q") String q,
                        @Param("categorySlug") String categorySlug,
                        @Param("material") String material,
                        @Param("frameShape") String frameShape,
                        @Param("isNew") Boolean isNew,
                        @Param("isBestSeller") Boolean isBestSeller,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        Pageable pageable);

        @Query("select p.name from Product p where p.published = true and (:q is null or lower(p.name) like lower(concat('%', :q, '%'))) order by p.name asc")
        List<String> suggestNames(@Param("q") String q, Pageable pageable);
}

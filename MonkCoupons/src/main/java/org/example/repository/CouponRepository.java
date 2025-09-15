package org.example.repository;

import org.example.model.Coupon;
import org.example.model.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByType(CouponType type);
    Optional<Coupon> findByTypeAndActiveTrue(CouponType type);
}
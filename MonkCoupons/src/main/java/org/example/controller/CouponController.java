package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.CartRequest;
import org.example.dto.CouponDao;
import org.example.dto.CartDto;
import org.example.dto.ResponseModel;
import org.example.model.Coupon;
import org.example.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseModel createCoupon(@RequestBody CouponDao couponDao){
        return couponService.createCoupon(couponDao);
    }

    @GetMapping
    public List<Coupon> getAllCoupons(){
        return couponService.getAllCoupons();
    }

    @GetMapping("/{id}")
    public Optional<Coupon> getCoupon(@PathVariable Long id){
        return couponService.getCouponById(id);
    }

    @PutMapping("/{id}")
    public ResponseModel updateCoupon(@PathVariable Long id, @RequestBody CouponDao couponDao){
        return couponService.updateCoupon(id, couponDao);
    }

    @DeleteMapping("/{id}")
    public ResponseModel deleteCoupon(@PathVariable Long id){
        return couponService.deleteCoupon(id);
    }

    @PostMapping("/applicable-coupons")
    public ResponseModel getApplicableCoupons(@RequestBody CartRequest cartDto) {

            return couponService.getApplicableCoupons(cartDto);
    }
    @PostMapping("/apply-coupon/{id}")
    public CartRequest applyCoupon(@PathVariable Long id, @RequestBody CartRequest cart){
        return couponService.applyCoupon(id, cart);
    }
}

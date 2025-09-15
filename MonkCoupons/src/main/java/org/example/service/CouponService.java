package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.model.Coupon;
import org.example.model.CouponType;
import org.example.repository.CouponRepository;
import org.example.utilis.CustomMessages;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final ObjectMapper objectMapper;

    // Create coupon
    public ResponseModel createCoupon(CouponDao couponDao) {
        try {
            CouponType type = CouponType.fromValue(couponDao.getType());

            Optional<Coupon> existing = couponRepository.findByTypeAndActiveTrue(type);
            if (existing.isPresent()) {
                return new ResponseModel(existing.get().getType(),
                        "Already Exist", CustomMessages.ALREADY_EXIST,
                        CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
            }

            Coupon coupon = new Coupon();
            coupon.setType(type);
            coupon.setActive(true);
            coupon.setExpiryDate(couponDao.getExpiryDate());

            if (couponDao.getDetails() != null) {
                String detailsJson = objectMapper.writeValueAsString(couponDao.getDetails());
                coupon.setDetailsJson(detailsJson);
            }

            Coupon saved = couponRepository.save(coupon);
            return new ResponseModel(saved.getType(),
                    "Coupon added successfully", CustomMessages.ADD_SUCCESSFULLY,
                    CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

        } catch (Exception e) {
            return new ResponseModel(null, "Error: " + e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
        }
    }

    // Get all coupons
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    // Get coupon by ID
    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    // Update coupon
    public ResponseModel updateCoupon(Long id, CouponDao couponDao) {
        try {
            Coupon existing = couponRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

            CouponType type = CouponType.fromValue(couponDao.getType());
            Optional<Coupon> duplicate = couponRepository.findByTypeAndActiveTrue(type);
            if(duplicate.isPresent() && !duplicate.get().getId().equals(id)){
                return new ResponseModel(type,
                        "Coupon of type " + couponDao.getType() + " already exists",
                        CustomMessages.ALREADY_EXIST, CustomMessages.FAILED,
                        CustomMessages.METHOD_PUT);
            }

            existing.setType(type);
            existing.setActive(couponDao.isActive());
            existing.setExpiryDate(couponDao.getExpiryDate());

            if(couponDao.getDetails() != null) {
                String detailsJson = objectMapper.writeValueAsString(couponDao.getDetails());
                existing.setDetailsJson(detailsJson);
            }

            Coupon updated = couponRepository.save(existing);
            return new ResponseModel(updated, "Coupon updated successfully",
                    CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_PUT);

        } catch (Exception e) {
            return new ResponseModel(null, "Error: " + e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_PUT);
        }
    }


    public ResponseModel deleteCoupon(Long id) {
        try {
            Coupon existing = couponRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

//            soft delete functionality
//            existing.setActive(false);
            couponRepository.delete(existing);

            return new ResponseModel(existing, "Coupon deleted successfully",
                    CustomMessages.DELETE_SUCCESSFULLY, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_DELETE);

        } catch (Exception e) {
            return new ResponseModel(null, "Error: " + e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_DELETE);
        }
    }

    public ResponseModel getApplicableCoupons(CartRequest cartDto) {
        try {
            List<Coupon> coupons = couponRepository.findAll();
            List<ApplicableCouponDto> applicableCoupons = new ArrayList<>();

            for (Coupon coupon : coupons) {
                // Deserialize detailsJson into CouponDetails DTO
                CouponDao.CouponDetails details = objectMapper.readValue(
                        coupon.getDetailsJson(),CouponDao.CouponDetails.class);

                double discount = 0;

                switch (coupon.getType()) {
                    case CART_WISE -> {
                        Integer threshold = details.getThreshold();
                        Integer percent = details.getDiscount();

                        double cartTotal = cartDto.getCart().getItems().stream()
                                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                                .sum();

                        if (threshold != null && percent != null && cartTotal >= threshold) {
                            discount = cartTotal * percent / 100.0;
                        }
                    }

                    case PRODUCT_WISE -> {
                        Long productId = details.getProductId();
                        Integer percent = details.getDiscount();

                        if (productId != null && percent != null) {
                            for (CartDto.CartItemDto item : cartDto.getCart().getItems()) {
                                if (item.getProductId().equals(productId)) {
                                    discount += item.getPrice() * item.getQuantity() * percent / 100.0;
                                }
                            }
                        }
                    }

                    case BXGY -> {

                        List<BxGyProductDto> buyProducts = details.getBuyProducts();
                        List<BxGyProductDto> getProducts = details.getGetProducts();

                        Integer repetitionLimit = details.getRepetitionLimit() != null ? details.getRepetitionLimit() : 1;

                        if (buyProducts != null && getProducts != null) {
                            int totalBuyQty = 0;

                            for (BxGyProductDto buy : buyProducts) {
                                Long pid = buy.getProductId();
                                int qty = buy.getQuantity();

                                int cartQty = cartDto.getCart().getItems().stream()
                                        .filter(i -> i.getProductId().equals(pid))
                                        .mapToInt(CartDto.CartItemDto::getQuantity)
                                        .sum();

                                totalBuyQty += cartQty / qty;
                            }

                            int applicableTimes = Math.min(totalBuyQty, repetitionLimit);

                            for (BxGyProductDto get : getProducts) {
                                Long pid = get.getProductId();
                                int qty = get.getQuantity() * applicableTimes;

                                Optional<CartDto.CartItemDto> cartItem = cartDto.getCart().getItems().stream()
                                        .filter(i -> i.getProductId().equals(pid))
                                        .findFirst();

                                if (cartItem.isPresent()) {
                                    discount += cartItem.get().getPrice() * qty;
                                }
                            }
                        }
                    }
                }

                if (discount > 0) {
                    applicableCoupons.add(new ApplicableCouponDto(
                            coupon.getId(),
                            coupon.getType(),
                            discount
                    ));
                }
            }

            return new ResponseModel(
                    applicableCoupons,
                    "Applicable coupons fetched successfully",
                    CustomMessages.GET_DATA_SUCCESS,
                    CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST
            );

        } catch (Exception e) {
            return new ResponseModel(
                    null,
                    "Error: " + e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR,
                    CustomMessages.FAILED,
                    CustomMessages.METHOD_POST
            );
        }
    }


    // Apply a specific coupon to the cart
    public CartRequest applyCoupon(Long couponId, CartRequest cart) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

            // If coupon is inactive, just return cart as is
            if (!coupon.isActive()) return cart;

            // Deserialize detailsJson into a generic map
            CouponDao.CouponDetails details = objectMapper.readValue(
                    coupon.getDetailsJson(),CouponDao.CouponDetails.class);

            double price = cart.getCart().getItems().stream()
                    .mapToDouble(i -> i.getPrice() * i.getQuantity())
                    .sum();
            double totalDiscount = 0;
            double finalPrice;

            switch (coupon.getType()) {

                case CART_WISE -> {
                    Integer threshold = details.getThreshold();
                    Integer discountPercent = details.getDiscount();

                    int totalPrice = cart.getCart().getItems().stream()
                            .mapToInt(i -> i.getPrice() * i.getQuantity())
                            .sum();
                  
                   

                    if (totalPrice >= threshold) {
                        int discount = totalPrice * discountPercent / 100;
                        totalDiscount += discount;
                        cart.getCart().getItems().forEach(i ->
                                i.setTotalDiscount(
                                        i.getTotalDiscount() != null
                                                ? i.getTotalDiscount() + discount
                                                : discount
                                )
                        );
                    }
                }

                case PRODUCT_WISE -> {
                    Long productId = details.getProductId();
                    Integer discountPercent = details.getDiscount();


                    for (CartDto.CartItemDto item : cart.getCart().getItems()) {
                        if (item.getProductId().equals(productId)) {
                            int discount = item.getPrice() * item.getQuantity() * discountPercent / 100;
                            totalDiscount += discount;
                            item.setTotalDiscount(
                                    item.getTotalDiscount() != null
                                            ? item.getTotalDiscount() + discount
                                            : discount
                            );
                        }
                    }
                }

                case BXGY -> {
                    List<BxGyProductDto> buyProducts = details.getBuyProducts();
                    List<BxGyProductDto> getProducts = details.getGetProducts();

                    Integer repetitionLimit = details.getRepetitionLimit() != null ? details.getRepetitionLimit() : 1;

                    // Calculate how many times condition is satisfied
                    int totalBuyQty = 0;
                    for (BxGyProductDto buy  : buyProducts) {
                        Long pid = buy.getProductId();
                        int qty = buy.getQuantity();


                        int cartQty = cart.getCart().getItems().stream()
                                .filter(i -> i.getProductId() == pid)
                                .mapToInt(CartDto.CartItemDto::getQuantity)
                                .sum();

                        totalBuyQty += cartQty / qty;
                    }

                    int applicableTimes = Math.min(totalBuyQty, repetitionLimit);

                    // Apply discount for "getProducts"

                    for (BxGyProductDto get : getProducts) {
                        Long pid = get.getProductId();
                        int qty = get.getQuantity() * applicableTimes;


                        int freeQty = qty * applicableTimes;

//                        cart.getCart().getItems().stream()
//                                .filter(i -> i.getProductId() == pid)
//                                .findFirst()
//                                .ifPresent(item -> {
//                                    int discount = item.getPrice() * Math.min(item.getQuantity(), freeQty);
//                                    totalDiscount += discount;
//                                    item.setTotalDiscount(
//                                            item.getTotalDiscount() != null
//                                                    ? item.getTotalDiscount() + discount
//                                                    : discount
//                                    );
//                                });

                        CartDto.CartItemDto item = cart.getCart().getItems().stream()
                                .filter(i -> i.getProductId().equals(pid))
                                .findFirst()
                                .orElse(null);

                        if (item != null) {
                            int discount = item.getPrice() * Math.min(item.getQuantity(), qty);
                            totalDiscount += discount;
                            item.setTotalDiscount(
                                    item.getTotalDiscount() != null
                                            ? item.getTotalDiscount() + discount
                                            : discount
                            );
                        }
                    }
                }
            }


            cart.setTotalPrice(price);
            cart.setTotalDiscount(totalDiscount);
            cart.setFinalPrice(price - totalDiscount);

            return cart;

        } catch (Exception e) {
            e.printStackTrace();
            return cart;
        }
    }

}

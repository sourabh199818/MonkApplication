package org.example.model;

import java.util.Arrays;

public enum CouponType {
    CART_WISE,
    PRODUCT_WISE,
    BXGY;

    public static CouponType fromValue(String value) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(value) || value.equalsIgnoreCase(t.name().replace("_","-")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid CouponType value: " + value));
    }
}

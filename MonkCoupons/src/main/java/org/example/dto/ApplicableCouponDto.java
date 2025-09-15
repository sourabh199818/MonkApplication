package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.model.CouponType;

@Data
@AllArgsConstructor
public class ApplicableCouponDto {
    private Long couponId;
    private CouponType type;
    private double discount;
}

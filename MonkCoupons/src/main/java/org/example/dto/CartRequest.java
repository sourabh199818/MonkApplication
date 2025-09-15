package org.example.dto;

import lombok.Data;

@Data
public class CartRequest {
    private CartDto cart;
    private double totalPrice;
    private double finalPrice;
    private double totalDiscount;


}

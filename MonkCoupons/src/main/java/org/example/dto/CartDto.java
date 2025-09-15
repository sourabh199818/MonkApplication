package org.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartDto {
    private List<CartItemDto> items;

    @Data
    public static class CartItemDto {
        private Long productId;
        private Integer quantity;
        private Integer price;
        private Integer totalDiscount;
    }
}

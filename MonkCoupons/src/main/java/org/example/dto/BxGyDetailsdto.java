package org.example.dto;


import lombok.Data;
import java.util.List;

@Data
public class BxGyDetailsdto {
    private List<CartItemDto> items;

    @Data
    public static class CartItemDto {
        private Long productId;
        private Integer quantity;
        private Integer price;
        private Integer totalDiscount;
    }
}

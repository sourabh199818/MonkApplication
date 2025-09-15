package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CouponDao {

    private Long id;
    private String type;
    private boolean active;
    private LocalDate expiryDate;

    private CouponDetails details;

    @Data
    public static class CouponDetails {

        private Integer threshold;
        private Integer discount;


        @JsonProperty("product_id")
        private Long productId;

        @JsonProperty("buy_products")
        private List<BxGyProductDto> buyProducts;

        @JsonProperty("get_products")
        private List<BxGyProductDto> getProducts;

        @JsonProperty("repition_limit")
        private Integer repetitionLimit;
    }


}

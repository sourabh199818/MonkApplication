package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BxGyProductDto {
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("quantity")
    private Integer quantity;
}

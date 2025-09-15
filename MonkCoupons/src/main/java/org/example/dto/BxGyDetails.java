package org.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class BxGyDetails {
    private List<BuyProduct> buy_products;
    private List<GetProduct> get_products;
    private int repetition_limit;

    @Data
    public static class BuyProduct {
        private Long productId;
        private int quantity;

        public BuyProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }

    @Data
    public static class GetProduct {
        private Long product_id;
        private int quantity;
    }

    public int getRepetition_limit() {
        return repetition_limit;
    }

}

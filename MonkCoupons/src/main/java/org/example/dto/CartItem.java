package org.example.dto;

public class CartItem {
    private Long productId;
    private Integer quantity;
    private Double price;
    private Double totalDiscount = 0.0;

    public CartItem() {}

    public CartItem(Long productId, Integer quantity, Double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(Double totalDiscount) { this.totalDiscount = totalDiscount; }

    public double totalPrice() { return price * quantity; }
}

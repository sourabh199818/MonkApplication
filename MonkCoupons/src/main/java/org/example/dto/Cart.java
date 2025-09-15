package org.example.dto;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double totalPrice() {
        return items.stream().mapToDouble(CartItem::totalPrice).sum();
    }

    public double totalDiscount() {
        return items.stream().mapToDouble(CartItem::getTotalDiscount).sum();
    }
}

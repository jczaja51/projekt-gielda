package com.stockmarket.logic;

import java.time.LocalDateTime;

public class Order {

    private final String symbol;
    private final OrderType type;
    private final int quantity;
    private final double limitPrice;
    private final LocalDateTime createdAt;

    public Order(String symbol, OrderType type, int quantity, double limitPrice) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol nie może być pusty.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Typ zlecenia nie może być null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ilość musi być dodatnia.");
        }
        if (limitPrice <= 0) {
            throw new IllegalArgumentException("Limit price musi być dodatni.");
        }

        this.symbol = symbol.trim().toUpperCase();
        this.type = type;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.createdAt = LocalDateTime.now();
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
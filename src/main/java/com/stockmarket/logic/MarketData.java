package com.stockmarket.logic;

import java.util.HashMap;
import java.util.Map;

public class MarketData {

    private final Map<String, Double> prices = new HashMap<>();

    public void setPrice(String symbol, double price) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol nie może być pusty.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Cena musi być dodatnia.");
        }
        prices.put(symbol.trim().toUpperCase(), price);
    }

    public double getPrice(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol nie może być pusty.");
        }

        Double price = prices.get(symbol.trim().toUpperCase());
        if (price == null) {
            throw new IllegalStateException("Brak ceny rynkowej dla: " + symbol);
        }
        return price;
    }
}
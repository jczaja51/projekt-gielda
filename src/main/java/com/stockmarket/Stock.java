package com.stockmarket;

import java.util.Objects;

public class Stock {

    private final String symbol;
    private final String name;
    private final double initialPrice;

    public Stock(String symbol, String name, double initialPrice) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol nie może być pusty ani null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa nie może być pusta ani null");
        }
        if (initialPrice <= 0) {
            throw new IllegalArgumentException("Cena początkowa musi być dodatnia");
        }

        this.symbol = symbol.toUpperCase();
        this.name = name;
        this.initialPrice = initialPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock other)) return false;
        return symbol.equalsIgnoreCase(other.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %.2f USD", name, symbol, initialPrice);
    }
}
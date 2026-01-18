package com.stockmarket.domain;

public class Currency extends Asset {

    private final double spread;

    public Currency(String symbol, String name, double basePrice, double spread) {
        super(symbol, name, basePrice);

        if (spread < 0) {
            throw new IllegalArgumentException("Spread nie może być ujemny.");
        }
        if (spread >= basePrice) {
            throw new IllegalArgumentException("Spread nie może być >= cenie bazowej (bid musi być dodatni).");
        }

        this.spread = spread;
    }

    @Override
    public double calculateRealValue(int quantity) {
        validateQuantity(quantity);

        double bid = getBasePrice() - spread;
        return bid * quantity;
    }

    @Override
    public double calculatePurchaseCost(int quantity) {
        validateQuantity(quantity);
        return getBasePrice() * quantity;
    }

    public double getSpread() {
        return spread;
    }
}
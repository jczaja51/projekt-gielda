package com.stockmarket.domain;

public class Share extends Asset {

    private static final double MANIPULATION_FEE = 3.0;

    public Share(String symbol, String name, double basePrice) {
        super(symbol, name, basePrice);
    }

    @Override
    public double calculateRealValue(int quantity) {
        validateQuantity(quantity);

        double gross = getBasePrice() * quantity;
        double net = gross - MANIPULATION_FEE;

        // Wartość rzeczywista nie może spaść poniżej zera
        return Math.max(0.0, net);
    }

    @Override
    public double calculatePurchaseCost(int quantity) {
        validateQuantity(quantity);

        return getBasePrice() * quantity + MANIPULATION_FEE;
    }
}
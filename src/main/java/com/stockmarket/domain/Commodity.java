package com.stockmarket.domain;

public class Commodity extends Asset {

    private final double storageCostPerUnitPerDay;

    public Commodity(String symbol, String name, double basePrice, double storageCostPerUnitPerDay) {
        super(symbol, name, basePrice);

        if (storageCostPerUnitPerDay < 0) {
            throw new IllegalArgumentException("Koszt magazynowania nie może być ujemny.");
        }

        this.storageCostPerUnitPerDay = storageCostPerUnitPerDay;
    }

    public double calculateRealValue(int quantity, int daysStored) {
        validateQuantity(quantity);

        if (daysStored < 0) {
            throw new IllegalArgumentException("Czas przechowywania nie może być ujemny.");
        }

        double gross = getBasePrice() * quantity;
        double storageLoss = storageCostPerUnitPerDay * quantity * daysStored;

        return Math.max(0.0, gross - storageLoss);
    }

    @Override
    public double calculateRealValue(int quantity) {
        return calculateRealValue(quantity, 0);
    }

    @Override
    public double calculatePurchaseCost(int quantity) {
        validateQuantity(quantity);
        return getBasePrice() * quantity;
    }

    public double getStorageCostPerUnitPerDay() {
        return storageCostPerUnitPerDay;
    }
}
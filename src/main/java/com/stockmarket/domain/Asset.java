package com.stockmarket.domain;

public abstract class Asset {

    private final String symbol;
    private final String name;
    private final double basePrice;

    public Asset(String symbol, String name, double basePrice) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol aktywa nie może być pusty.");
        }

        String normalizedSymbol = symbol.trim().toUpperCase();
        if (!normalizedSymbol.matches("[A-Z]{3,6}")) {
            throw new IllegalArgumentException(
                    "Symbol aktywa musi składać się z 3–6 wielkich liter (A–Z)."
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa aktywa nie może być pusta.");
        }
        if (basePrice <= 0) {
            throw new IllegalArgumentException("Cena bazowa musi być większa od zera.");
        }

        this.symbol = normalizedSymbol;
        this.name = name.trim();
        this.basePrice = basePrice;
    }

    protected void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ilość musi być większa od zera.");
        }
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }

    public abstract double calculateRealValue(int quantity);
    public abstract double calculatePurchaseCost(int quantity);
}
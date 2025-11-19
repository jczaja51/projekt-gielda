package com.stockmarket;

public class Portfolio {

    private double cash;
    private final StockHolding[] holdings;
    private int holdingsCount;
    private static final int MAX_HOLDINGS = 10;

    private static class StockHolding {
        private final Stock stock;
        private int quantity;

        public StockHolding(Stock stock, int quantity) {
            if (stock == null) {
                throw new IllegalArgumentException("Stock nie może być null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Ilość musi być dodatnia");
            }

            this.stock = stock;
            this.quantity = quantity;
        }
    }

    public Portfolio(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Początkowa gotówka nie może być ujemna");
        }
        this.cash = initialCash;
        this.holdings = new StockHolding[MAX_HOLDINGS];
        this.holdingsCount = 0;
    }

    public int addStock(Stock stock, int quantity) {
        if (stock == null) throw new IllegalArgumentException("Stock nie może być null");
        if (quantity <= 0) throw new IllegalArgumentException("Ilość musi być dodatnia");

        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].stock.equals(stock)) {
                holdings[i].quantity += quantity;
                return holdings[i].quantity;
            }
        }

        if (holdingsCount >= MAX_HOLDINGS) {
            throw new IllegalStateException("Brak miejsca na nowe akcje w portfelu");
        }

        holdings[holdingsCount++] = new StockHolding(stock, quantity);
        return quantity;
    }

    public double calculateStockValue() {
        double total = 0.0;
        for (int i = 0; i < holdingsCount; i++) {
            total += holdings[i].stock.getInitialPrice() * holdings[i].quantity;
        }
        return total;
    }

    public double calculateTotalValue() {
        return cash + calculateStockValue();
    }

    public int getHoldingsCount() {
        return holdingsCount;
    }

    public int getStockQuantity(Stock stock) {
        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].stock.equals(stock)) {
                return holdings[i].quantity;
            }
        }
        return 0;
    }

    public double getCash() {
        return cash;
    }
}
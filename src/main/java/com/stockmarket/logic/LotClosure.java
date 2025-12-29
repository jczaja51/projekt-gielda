package com.stockmarket.logic;

import java.time.LocalDate;

public class LotClosure {

    private final LocalDate purchaseDate;
    private final int quantitySold;
    private final double profit;

    public LotClosure(LocalDate purchaseDate, int quantitySold, double profit) {
        this.purchaseDate = purchaseDate;
        this.quantitySold = quantitySold;
        this.profit = profit;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getProfit() {
        return profit;
    }
}
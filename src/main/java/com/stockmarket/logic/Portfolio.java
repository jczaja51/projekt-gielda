package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private double cash;
    private final List<AssetHolding> holdings = new ArrayList<>();

    private static class AssetHolding {
        private final Asset asset;
        private int quantity;

        private AssetHolding(Asset asset, int quantity) {
            if (asset == null) {
                throw new IllegalArgumentException("Aktywo nie może być null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Ilość musi być dodatnia");
            }
            this.asset = asset;
            this.quantity = quantity;
        }
    }

    public Portfolio(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Początkowa gotówka nie może być ujemna");
        }
        this.cash = initialCash;
    }

    public double getCash() {
        return cash;
    }

    public int getAssetQuantity(Asset asset) {
        for (AssetHolding h : holdings) {
            if (h.asset.equals(asset)) return h.quantity;
        }
        return 0;
    }

    public void buyAsset(Asset asset, int quantity) {
        if (asset == null) throw new IllegalArgumentException("Aktywo null");
        if (quantity <= 0) throw new IllegalArgumentException("Ilość musi być dodatnia");

        double cost = asset.calculatePurchaseCost(quantity);

        if (cost > cash)
            throw new IllegalStateException("Brak środków na zakup: potrzebne " + cost);

        cash -= cost;

        for (AssetHolding h : holdings) {
            if (h.asset.equals(asset)) {
                h.quantity += quantity;
                return;
            }
        }

        holdings.add(new AssetHolding(asset, quantity));
    }

    public double calculateTotalAssetsRealValue() {
        double total = 0;
        for (AssetHolding h : holdings) {

            total += h.asset.calculateRealValue(h.quantity);
        }
        return total;
    }

    public double calculateTotalValue() {
        return cash + calculateTotalAssetsRealValue();
    }

    public String auditReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== AUDYT PORTFELA ===\n");
        sb.append("Gotówka: ").append(cash).append("\n\n");

        for (AssetHolding h : holdings) {
            double realVal = h.asset.calculateRealValue(h.quantity);
            sb.append(h.asset.getName())
                    .append(" (").append(h.asset.getSymbol()).append(")")
                    .append(" | ilość: ").append(h.quantity)
                    .append(" | wartość rzeczywista: ").append(realVal)
                    .append("\n");
        }

        sb.append("\nŁączna wartość aktywów: ")
                .append(calculateTotalAssetsRealValue())
                .append("\nŁączna wartość portfela: ")
                .append(calculateTotalValue());

        return sb.toString();
    }
}
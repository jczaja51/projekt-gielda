package com.stockmarket.logic;

import com.stockmarket.domain.Asset;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AssetPosition {

    private final Asset asset;

    private final PriorityQueue<PurchaseLot> lots =
            new PriorityQueue<>(Comparator.comparing(PurchaseLot::getPurchaseDate));

    private int totalQuantity = 0;

    public AssetPosition(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset nie może być null.");
        }
        this.asset = asset;
    }

    public Asset getAsset() {
        return asset;
    }

    public void addLot(PurchaseLot lot) {
        if (lot == null) {
            throw new IllegalArgumentException("PurchaseLot nie może być null.");
        }
        lots.add(lot);
        totalQuantity += lot.getQuantity();
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public PurchaseLot peekOldestLot() {
        return lots.peek();
    }

    public PurchaseLot pollOldestLot() {
        return lots.poll();
    }

    public void decreaseTotalQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Zmniejszenie ilości musi być dodatnie.");
        }
        if (amount > totalQuantity) {
            throw new IllegalArgumentException("Nie można zmniejszyć poniżej zera.");
        }
        totalQuantity -= amount;
    }

    public PriorityQueue<PurchaseLot> getLots() {
        return new PriorityQueue<>(lots);
    }
}
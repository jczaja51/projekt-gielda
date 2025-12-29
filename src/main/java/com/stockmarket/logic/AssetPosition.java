package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import java.util.ArrayList;
import java.util.List;

public class AssetPosition {
    private final Asset asset;
    private final List<PurchaseLot> lots = new ArrayList<>();

    public AssetPosition(Asset asset) {
        if (asset == null) throw new IllegalArgumentException("Aktywo nie może być null.");
        this.asset = asset;
    }

    public Asset getAsset() { return asset; }
    public List<PurchaseLot> getLots() { return lots; }

    public int getTotalQuantity() {
        int sum = 0;
        for (int i = 0; i < lots.size(); i++) {
            sum += lots.get(i).getQuantity();
        }
        return sum;
    }

    public void addLot(PurchaseLot lot) {
        if (lot == null) throw new IllegalArgumentException("Partia nie może być null.");
        lots.add(lot);
    }
}
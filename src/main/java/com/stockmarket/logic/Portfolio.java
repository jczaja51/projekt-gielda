package com.stockmarket.logic;

import com.stockmarket.domain.Asset;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private double cash;

    private final Map<String, AssetPosition> positions = new HashMap<>();

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
        if (asset == null) throw new IllegalArgumentException("Aktywo null");
        AssetPosition pos = positions.get(asset.getSymbol());
        return (pos == null) ? 0 : pos.getTotalQuantity();
    }

    public void buyAsset(Asset asset, int quantity) {
        if (asset == null) throw new IllegalArgumentException("Aktywo null");
        if (quantity <= 0) throw new IllegalArgumentException("Ilość musi być dodatnia");

        double cost = asset.calculatePurchaseCost(quantity);

        if (cost > cash) {
            throw new IllegalStateException("Brak środków na zakup: potrzebne " + cost);
        }

        cash -= cost;

        AssetPosition pos = positions.get(asset.getSymbol());
        if (pos == null) {
            pos = new AssetPosition(asset);
            positions.put(asset.getSymbol(), pos);
        }

        PurchaseLot lot = new PurchaseLot(LocalDate.now(), quantity, asset.getBasePrice());
        pos.addLot(lot);
    }

    public double calculateTotalAssetsRealValue() {
        double total = 0.0;

        for (AssetPosition pos : positions.values()) {
            int qty = pos.getTotalQuantity();
            if (qty > 0) {
                total += pos.getAsset().calculateRealValue(qty);
            }
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

        for (AssetPosition pos : positions.values()) {
            Asset asset = pos.getAsset();
            int qty = pos.getTotalQuantity();
            double realVal = asset.calculateRealValue(qty);

            sb.append(asset.getName())
                    .append(" (").append(asset.getSymbol()).append(")")
                    .append(" | ilość: ").append(qty)
                    .append(" | wartość rzeczywista: ").append(realVal)
                    .append("\n");

            for (int i = 0; i < pos.getLots().size(); i++) {
                PurchaseLot lot = pos.getLots().get(i);
                sb.append("  LOT | ").append(lot.getPurchaseDate())
                        .append(" | qty: ").append(lot.getQuantity())
                        .append(" | price: ").append(lot.getUnitPrice())
                        .append("\n");
            }
        }

        sb.append("\nŁączna wartość aktywów: ")
                .append(calculateTotalAssetsRealValue())
                .append("\nŁączna wartość portfela: ")
                .append(calculateTotalValue());

        return sb.toString();
    }
}

public SellResult sellAssetFIFO(String symbol, int quantity, double sellPrice) {
    if (symbol == null || symbol.isBlank())
        throw new IllegalArgumentException("Symbol nie może być pusty.");
    if (quantity <= 0)
        throw new IllegalArgumentException("Ilość musi być dodatnia.");
    if (sellPrice <= 0)
        throw new IllegalArgumentException("Cena sprzedaży musi być dodatnia.");

    AssetPosition position = positions.get(symbol.trim().toUpperCase());
    if (position == null || position.getTotalQuantity() < quantity) {
        throw new InsufficientHoldingsException(
                "Brak wystarczającej ilości aktywa: " + symbol
        );
    }

    int remainingToSell = quantity;
    SellResult result = new SellResult();

    for (int i = 0; i < position.getLots().size() && remainingToSell > 0; i++) {
        PurchaseLot lot = position.getLots().get(i);

        int available = lot.getQuantity();
        int used = Math.min(available, remainingToSell);

        double profit = used * (sellPrice - lot.getUnitPrice());

        result.addClosure(
                new LotClosure(
                        lot.getPurchaseDate(),
                        used,
                        profit
                )
        );

        lot.decreaseQuantity(used);
        remainingToSell -= used;
    }

    position.getLots().removeIf(lot -> lot.getQuantity() == 0);

    cash += quantity * sellPrice;

    return result;
}

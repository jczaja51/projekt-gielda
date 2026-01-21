package com.stockmarket.logic;

import com.stockmarket.domain.Asset;

import java.time.LocalDate;
import java.util.*;

public class Portfolio {

    private double cash;

    private final Map<String, AssetPosition> positions = new HashMap<>();

    private final Set<String> watchlist = new HashSet<>();

    private final MarketData marketData;

    private final PriorityQueue<Order> orders;

    public Portfolio(double initialCash, MarketData marketData) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Początkowa gotówka nie może być ujemna.");
        }
        if (marketData == null) {
            throw new IllegalArgumentException("MarketData nie może być null.");
        }

        this.cash = initialCash;
        this.marketData = marketData;
        this.orders = new PriorityQueue<>(new OrderComparator(marketData));
    }

    public Portfolio(double initialCash) {
        this(initialCash, new MarketData());
    }

    public double getCash() {
        return cash;
    }

    public void buyAsset(Asset asset, int quantity) {
        if (asset == null) {
            throw new IllegalArgumentException("Aktywo nie może być null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ilość musi być dodatnia.");
        }

        double cost = asset.calculatePurchaseCost(quantity);
        if (cost > cash) {
            throw new IllegalStateException("Brak środków na zakup: potrzebne " + cost);
        }

        cash -= cost;

        String key = normalizeSymbol(asset.getSymbol());
        AssetPosition pos = positions.get(key);
        if (pos == null) {
            pos = new AssetPosition(asset);
            positions.put(key, pos);
        }

        pos.addLot(new PurchaseLot(LocalDate.now(), quantity, asset.getBasePrice()));
    }

    public SellResult sellAssetFIFO(String symbol, int quantity, double sellPrice) {
        if (quantity <= 0 || sellPrice <= 0) {
            throw new IllegalArgumentException("Niepoprawne dane sprzedaży.");
        }

        AssetPosition position = positions.get(normalizeSymbol(symbol));
        if (position == null || position.getTotalQuantity() < quantity) {
            throw new InsufficientHoldingsException("Brak wystarczającej ilości aktywa.");
        }

        SellResult result = new SellResult();
        int remaining = quantity;

        while (remaining > 0) {
            PurchaseLot lot = position.peekOldestLot();

            int used = Math.min(lot.getQuantity(), remaining);

            double profit = used * (sellPrice - lot.getUnitPrice());
            result.addClosure(new LotClosure(lot.getPurchaseDate(), used, profit));

            lot.decreaseQuantity(used);
            position.decreaseTotalQuantity(used);
            remaining -= used;

            if (lot.getQuantity() == 0) {
                position.pollOldestLot();
            }
        }

        cash += quantity * sellPrice;
        return result;
    }

    public boolean addToWatchlist(String symbol) {
        String key = normalizeSymbol(symbol);
        return watchlist.add(key);
    }

    public void placeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order nie może być null.");
        }
        orders.add(order);
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

    public void putPositionForLoad(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Aktywo nie może być null.");
        }
        String key = normalizeSymbol(asset.getSymbol());
        if (!positions.containsKey(key)) {
            positions.put(key, new AssetPosition(asset));
        }
    }

    public AssetPosition getPositionBySymbol(String symbol) {
        String key = normalizeSymbol(symbol);
        return positions.get(key);
    }

    public Iterable<AssetPosition> getPositions() {
        return new ArrayList<>(positions.values());
    }

    private String normalizeSymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol nie może być pusty.");
        }
        return symbol.trim().toUpperCase();
    }
}
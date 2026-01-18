package com.stockmarket.logic;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

    private final MarketData marketData;

    public OrderComparator(MarketData marketData) {
        if (marketData == null) {
            throw new IllegalArgumentException("MarketData nie może być null.");
        }
        this.marketData = marketData;
    }

    @Override
    public int compare(Order a, Order b) {
        if (a == b) return 0;
        if (a == null) return 1;
        if (b == null) return -1;

        if (a.getType() != b.getType()) {
            return a.getType().compareTo(b.getType());
        }

        int sym = a.getSymbol().compareTo(b.getSymbol());
        if (sym != 0) {
            return sym;
        }

        int priceCmp;
        if (a.getType() == OrderType.BUY) {
            priceCmp = Double.compare(b.getLimitPrice(), a.getLimitPrice());
        } else {
            priceCmp = Double.compare(a.getLimitPrice(), b.getLimitPrice());
        }
        if (priceCmp != 0) return priceCmp;

        double marketPrice = safeMarketPrice(a.getSymbol());
        if (!Double.isNaN(marketPrice)) {
            double aScore = attractivenessScore(a, marketPrice);
            double bScore = attractivenessScore(b, marketPrice);

            int scoreCmp = Double.compare(bScore, aScore);
            if (scoreCmp != 0) return scoreCmp;
        }

        return a.getCreatedAt().compareTo(b.getCreatedAt());
    }

    private double safeMarketPrice(String symbol) {
        try {
            return marketData.getPrice(symbol);
        } catch (RuntimeException ex) {
            return Double.NaN;
        }
    }

    private double attractivenessScore(Order order, double marketPrice) {
        if (order.getType() == OrderType.BUY) {
            return order.getLimitPrice() - marketPrice;
        } else {
            return marketPrice - order.getLimitPrice();
        }
    }
}
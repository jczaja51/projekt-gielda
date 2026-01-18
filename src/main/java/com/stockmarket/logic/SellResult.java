package com.stockmarket.logic;

import java.util.ArrayList;
import java.util.List;

public class SellResult {

    private double totalProfit;
    private final List<LotClosure> closures = new ArrayList<>();

    public void addClosure(LotClosure closure) {
        if (closure == null) {
            throw new IllegalArgumentException("LotClosure nie może być null.");
        }
        closures.add(closure);
        totalProfit += closure.getProfit();
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public List<LotClosure> getClosures() {
        return List.copyOf(closures);
    }
}
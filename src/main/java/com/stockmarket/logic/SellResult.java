package com.stockmarket.logic;

import java.util.ArrayList;
import java.util.List;

public class SellResult {

    private double totalProfit;
    private final List<LotClosure> closures = new ArrayList<>();

    public void addClosure(LotClosure closure) {
        closures.add(closure);
        totalProfit += closure.getProfit();
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public List<LotClosure> getClosures() {
        return closures;
    }
}
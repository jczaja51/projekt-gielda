package com.stockmarket;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolymorphismTest {

    @Test
    void portfolioUsesPolymorphicAssetValuation() {
        Portfolio portfolio = new Portfolio(10_000);

        Asset[] assets = {
                new Share("ASD", "Share", 100.0),
                new Currency("USD", "USD", 100.0, 1.0),
                new Commodity("GLD", "Gold", 100.0, 2.0)
        };

        int quantity = 10;
        double expectedTotal = 0.0;

        for (Asset asset : assets) {
            portfolio.buyAsset(asset, quantity);
            expectedTotal += asset.calculateRealValue(quantity);
        }

        double actualTotal = portfolio.calculateTotalAssetsRealValue();

        assertEquals(
                expectedTotal,
                actualTotal,
                0.0001
        );
    }

    @Test
    void differentAssetTypesApplyDifferentBusinessRules() {
        int quantity = 10;
        double basePrice = 100.0;

        Asset share = new Share("AAA", "Share", basePrice);
        Asset commodity = new Commodity("BBB", "Commodity", basePrice, 2.0);
        Asset currency = new Currency("CCC", "Currency", basePrice, 1.0);

        double shareValue = share.calculateRealValue(quantity);
        double commodityValue = commodity.calculateRealValue(quantity);
        double currencyValue = currency.calculateRealValue(quantity);

        assertTrue(
                shareValue != commodityValue &&
                        shareValue != currencyValue &&
                        commodityValue != currencyValue,
                "Każdy typ aktywa powinien stosować inną logikę wyceny"
        );
    }
}
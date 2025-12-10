package com.stockmarket;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioIntegrationTest {

    @Test
    void totalValueIsCashPlusRealAssetValues() {
        Portfolio p = new Portfolio(5000.0);

        Share share = new Share("AAA", "Firma AAA", 50.0);
        Commodity commodity = new Commodity("OIL", "Ropa", 80.0, 2.0);
        Currency currency = new Currency("EUR", "Euro", 4.50, 0.10);

        p.buyAsset(share, 10);
        p.buyAsset(commodity, 5);
        p.buyAsset(currency, 100);

        double realAssetsValue = p.calculateTotalAssetsRealValue();
        double totalValue = p.calculateTotalValue();

        assertEquals(p.getCash() + realAssetsValue, totalValue, 0.0001);
    }

    @Test
    void increasesQuantityWhenBuyingSameAssetAgain() {
        Portfolio p = new Portfolio(10_000);

        Share s = new Share("X", "XCorp", 20);

        p.buyAsset(s, 5);
        p.buyAsset(s, 3);

        assertEquals(8, p.getAssetQuantity(s));
    }
}
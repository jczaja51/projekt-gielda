package com.stockmarket;

import com.stockmarket.domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolymorphismTest {

    @Test
    void testDifferentRealValuesForDifferentMarkets() {
        int quantity = 10;

        Asset share = new Share("AAA", "TestShare", 100.0);
        Asset commodity = new Commodity("BBB", "TestGold", 100.0, 2.0);
        Asset currency = new Currency("CCC", "TestCurrency", 100.0, 1.0);

        double shareValue = share.calculateRealValue(quantity);
        double commodityValue = commodity.calculateRealValue(quantity);
        double currencyValue = currency.calculateRealValue(quantity);

        assertNotEquals(shareValue, commodityValue,
                "Akcja i surowiec nie mogą mieć tej samej wartości końcowej!");
        assertNotEquals(shareValue, currencyValue,
                "Akcja i waluta nie mogą mieć tej samej wartości końcowej!");
        assertNotEquals(commodityValue, currencyValue,
                "Surowiec i waluta nie mogą mieć tej samej wartością!");

        assertTrue(shareValue > 0);
        assertTrue(commodityValue > 0);
        assertTrue(currencyValue > 0);
    }
}
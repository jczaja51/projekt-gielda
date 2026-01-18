package com.stockmarket;

import com.stockmarket.logic.MarketData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataTest {

    @Test
    void setAndGetPrice_returnsCorrectValue() {
        MarketData marketData = new MarketData();

        marketData.setPrice("USD", 4.25);
        double price = marketData.getPrice("USD");

        assertEquals(4.25, price);
    }

    @Test
    void symbolIsNormalized_trimAndUppercase() {
        MarketData marketData = new MarketData();

        marketData.setPrice(" usd ", 4.5);

        assertEquals(4.5, marketData.getPrice("USD"));
        assertEquals(4.5, marketData.getPrice(" usd "));
    }

    @Test
    void setPrice_nullSymbol_throwsException() {
        MarketData marketData = new MarketData();

        assertThrows(
                IllegalArgumentException.class,
                () -> marketData.setPrice(null, 1.0)
        );
    }

    @Test
    void setPrice_blankSymbol_throwsException() {
        MarketData marketData = new MarketData();

        assertThrows(
                IllegalArgumentException.class,
                () -> marketData.setPrice("   ", 1.0)
        );
    }

    @Test
    void setPrice_nonPositivePrice_throwsException() {
        MarketData marketData = new MarketData();

        assertThrows(
                IllegalArgumentException.class,
                () -> marketData.setPrice("USD", 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> marketData.setPrice("USD", -1)
        );
    }

    @Test
    void getPrice_missingPrice_throwsException() {
        MarketData marketData = new MarketData();

        assertThrows(
                IllegalStateException.class,
                () -> marketData.getPrice("EUR")
        );
    }

    @Test
    void getPrice_blankSymbol_throwsException() {
        MarketData marketData = new MarketData();

        assertThrows(
                IllegalArgumentException.class,
                () -> marketData.getPrice(" ")
        );
    }
}
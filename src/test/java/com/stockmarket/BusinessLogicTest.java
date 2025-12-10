package com.stockmarket;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.Share;
import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessLogicTest {

    @Test
    void testCannotBuyAssetIfInsufficientFunds() {
        Portfolio portfolio = new Portfolio(100.0);
        Asset share = new Share("AAA", "TestShare", 100.0);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> portfolio.buyAsset(share, 1)
        );

        assertTrue(exception.getMessage().startsWith("Brak środków na zakup"));
    }
}
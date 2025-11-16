package com.stockmarket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    private Portfolio portfolio;
    private Stock cdr;
    private Stock plw;
    private Stock ten;

    @BeforeEach
    void setup() {
        portfolio = new Portfolio(1000.0);
        cdr = new Stock("CDR", "CD Projekt", 100.0);
        plw = new Stock("PLW", "PlayWay", 50.0);
        ten = new Stock("TEN", "Ten Square Games", 200.0);
    }

    @Test
    void shouldInitializeWithCorrectCashAndEmptyHoldings() {
        assertEquals(1000.0, portfolio.getCash());
        assertEquals(0, portfolio.getHoldingsCount());
        assertEquals(0, portfolio.getStockQuantity(cdr));
    }

    @Test
    void shouldReturnZeroStockValueForEmptyPortfolio() {
        assertEquals(0.0, portfolio.calculateStockValue());
    }

    @Test
    void shouldAddNewStockWhenNotPresent() {
        portfolio.addStock(cdr, 5);
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(5, portfolio.getStockQuantity(cdr));
    }

    @Test
    void shouldIncreaseQuantityWhenStockAlreadyExists() {
        portfolio.addStock(cdr, 3);
        portfolio.addStock(cdr, 2);
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(5, portfolio.getStockQuantity(cdr));
    }

    @Test
    void shouldAddMultipleDifferentStocks() {
        portfolio.addStock(cdr, 2);
        portfolio.addStock(plw, 4);
        portfolio.addStock(ten, 1);

        assertEquals(3, portfolio.getHoldingsCount());
        assertEquals(2, portfolio.getStockQuantity(cdr));
        assertEquals(4, portfolio.getStockQuantity(plw));
        assertEquals(1, portfolio.getStockQuantity(ten));
    }

    @Test
    void shouldCalculateCorrectStockValue() {
        portfolio.addStock(cdr, 2);
        portfolio.addStock(plw, 3);
        assertEquals(350.0, portfolio.calculateStockValue());
    }

    @Test
    void shouldCalculateTotalValueIncludingCash() {
        portfolio.addStock(cdr, 2);
        portfolio.addStock(plw, 3);
        assertEquals(1350.0, portfolio.calculateTotalValue());
    }

    @Test
    void shouldThrowWhenAddingNullStock() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.addStock(null, 5));
    }

    @Test
    void shouldThrowWhenAddingNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.addStock(cdr, -1));
    }

    @Test
    void shouldThrowWhenPortfolioIsFull() {
        for (int i = 0; i < 10; i++) {
            Stock s = new Stock("MFG" + i, "Zespół mfg " + i, 10.0 * (i + 1));
            portfolio.addStock(s, 1);
        }

        Stock extra = new Stock("EXTRA", "Extra-masło", 50.0);
        assertThrows(IllegalStateException.class, () -> portfolio.addStock(extra, 1));
    }
}
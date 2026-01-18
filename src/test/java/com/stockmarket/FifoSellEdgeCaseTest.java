package com.stockmarket;

import com.stockmarket.domain.Share;
import com.stockmarket.logic.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FifoSellEdgeCaseTest {

    @Test
    void sellingZeroQuantityIsRejected() {
        Portfolio portfolio = new Portfolio(0);
        Share share = new Share("ZERO", "Test", 1.0);

        portfolio.putPositionForLoad(share);

        assertThrows(
                IllegalArgumentException.class,
                () -> portfolio.sellAssetFIFO("ZERO", 0, 100)
        );
    }

    @Test
    void fifoWorksWithSingleLotOnly() {
        Portfolio portfolio = new Portfolio(0);
        Share share = new Share("ONE", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("ONE");

        pos.addLot(new PurchaseLot(LocalDate.now(), 10, 100));

        SellResult result = portfolio.sellAssetFIFO("ONE", 5, 120);

        assertEquals(5 * 20, result.getTotalProfit(), 0.0001);
        assertEquals(5, pos.getTotalQuantity());
    }

    @Test
    void fifoRespectsExactBoundaryBetweenLots() {
        Portfolio portfolio = new Portfolio(0);
        Share share = new Share("BOUND", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("BOUND");

        pos.addLot(new PurchaseLot(LocalDate.of(2023, 1, 1), 5, 100));
        pos.addLot(new PurchaseLot(LocalDate.of(2023, 1, 2), 5, 110));

        SellResult result = portfolio.sellAssetFIFO("BOUND", 5, 130);

        assertEquals(5 * 30, result.getTotalProfit(), 0.0001);
        assertEquals(5, pos.getTotalQuantity());
    }

    @Test
    void fifoUsesDateNotInsertionOrderAtBoundary() {
        Portfolio portfolio = new Portfolio(0);
        Share share = new Share("DATE", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("DATE");

        pos.addLot(new PurchaseLot(LocalDate.of(2023, 2, 1), 5, 120));
        pos.addLot(new PurchaseLot(LocalDate.of(2023, 1, 1), 5, 100));

        SellResult result = portfolio.sellAssetFIFO("DATE", 5, 150);

        assertEquals(5 * 50, result.getTotalProfit(), 0.0001);
    }

    @Test
    void sellingMoreThanOwnedDoesNotModifyState() {
        Portfolio portfolio = new Portfolio(0);
        Share share = new Share("ERR", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("ERR");

        pos.addLot(new PurchaseLot(LocalDate.now(), 3, 100));

        assertThrows(
                InsufficientHoldingsException.class,
                () -> portfolio.sellAssetFIFO("ERR", 4, 120)
        );

        assertEquals(3, pos.getTotalQuantity());
    }
}
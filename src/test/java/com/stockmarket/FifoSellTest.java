package com.stockmarket;

import com.stockmarket.domain.Share;
import com.stockmarket.logic.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FifoSellTest {

    @Test
    void fifoSellsOldestPurchaseFirst_regardlessOfInsertionOrder() {
        Portfolio portfolio = new Portfolio(10_000);
        Share share = new Share("XYZ", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("XYZ");

        pos.addLot(new PurchaseLot(LocalDate.of(2023, 2, 1), 10, 120));
        pos.addLot(new PurchaseLot(LocalDate.of(2023, 1, 1), 10, 100));

        SellResult result = portfolio.sellAssetFIFO("XYZ", 10, 150);

        assertEquals(
                500.0,
                result.getTotalProfit(),
                0.0001,
                "FIFO should sell the oldest purchase first (by date)"
        );
    }

    @Test
    void fifoUsesMultipleLotsWhenSingleLotIsNotEnough() {
        Portfolio portfolio = new Portfolio(10_000);
        Share share = new Share("ABC", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("ABC");

        pos.addLot(new PurchaseLot(LocalDate.of(2023, 1, 1), 5, 100));
        pos.addLot(new PurchaseLot(LocalDate.of(2023, 2, 1), 10, 120));

        SellResult result = portfolio.sellAssetFIFO("ABC", 8, 150);

        assertEquals(5 * 50 + 3 * 30, result.getTotalProfit(), 0.0001);
        assertEquals(7, pos.getTotalQuantity());
    }

    @Test
    void sellingExactTotalQuantityLeavesPositionEmpty() {
        Portfolio portfolio = new Portfolio(10_000);
        Share share = new Share("ALL", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("ALL");

        pos.addLot(new PurchaseLot(LocalDate.now(), 4, 100));
        pos.addLot(new PurchaseLot(LocalDate.now(), 6, 100));

        portfolio.sellAssetFIFO("ALL", 10, 120);

        assertEquals(
                0,
                pos.getTotalQuantity(),
                "Position should be empty after selling all owned quantity"
        );
    }

    @Test
    void fifoDoesNotModifyStateWhenSellingMoreThanOwned() {
        Portfolio portfolio = new Portfolio(10_000);
        Share share = new Share("ERR", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("ERR");
        pos.addLot(new PurchaseLot(LocalDate.now(), 5, 100));

        assertThrows(
                InsufficientHoldingsException.class,
                () -> portfolio.sellAssetFIFO("ERR", 6, 150)
        );

        assertEquals(
                5,
                pos.getTotalQuantity(),
                "Position state should remain unchanged after failed sell"
        );
    }

    @Test
    void sellingFromSingleLotUsesThatLotOnly() {
        Portfolio portfolio = new Portfolio(10_000);
        Share share = new Share("ONE", "Test", 1.0);

        portfolio.putPositionForLoad(share);
        AssetPosition pos = portfolio.getPositionBySymbol("ONE");
        pos.addLot(new PurchaseLot(LocalDate.now(), 10, 100));

        SellResult result = portfolio.sellAssetFIFO("ONE", 3, 130);

        assertEquals(3 * 30, result.getTotalProfit(), 0.0001);
        assertEquals(7, pos.getTotalQuantity());
    }

    @Test
    void invalidSellArgumentsAreRejected() {
        Portfolio portfolio = new Portfolio(10_000);

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.sellAssetFIFO(null, 1, 100));

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.sellAssetFIFO("AAA", 0, 100));

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.sellAssetFIFO("AAA", 1, -1));
    }
}
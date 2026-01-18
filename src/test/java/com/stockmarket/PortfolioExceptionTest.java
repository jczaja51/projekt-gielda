package com.stockmarket;

import com.stockmarket.domain.Share;
import com.stockmarket.logic.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioExceptionTest {

    @Test
    void buyingAssetWithoutEnoughCashThrowsExceptionAndDoesNotChangeCash() {
        Portfolio portfolio = new Portfolio(100);
        Share share = new Share("AAA", "TestShare", 100);

        assertThrows(
                IllegalStateException.class,
                () -> portfolio.buyAsset(share, 2),
                "Zakup przy braku środków powinien rzucić wyjątek"
        );

        assertEquals(
                100,
                portfolio.getCash(),
                0.0001,
                "Stan gotówki nie powinien się zmienić po nieudanym zakupie"
        );
    }

    @Test
    void sellingNonExistingAssetThrowsInsufficientHoldingsException() {
        Portfolio portfolio = new Portfolio(0);

        assertThrows(
                InsufficientHoldingsException.class,
                () -> portfolio.sellAssetFIFO("XYZ", 1, 100),
                "Sprzedaż nieposiadanego aktywa powinna rzucić wyjątek"
        );
    }

    @Test
    void sellingMoreThanOwnedThrowsExceptionAndDoesNotModifyLots() {
        Portfolio portfolio = new Portfolio(0);

        Share share = new Share("AAA", "Share", 100);
        portfolio.putPositionForLoad(share);

        AssetPosition pos = portfolio.getPositionBySymbol("AAA");
        pos.addLot(new PurchaseLot(LocalDate.now(), 5, 100));

        assertThrows(
                InsufficientHoldingsException.class,
                () -> portfolio.sellAssetFIFO("AAA", 10, 120),
                "Sprzedaż większej ilości niż posiadana powinna rzucić wyjątek"
        );

        assertEquals(
                5,
                pos.getTotalQuantity(),
                "Stan partii nie powinien się zmienić po nieudanej sprzedaży"
        );

        assertEquals(
                1,
                pos.getLots().size(),
                "Lista lotów nie powinna zostać zmodyfikowana"
        );
    }

    @Test
    void invalidArgumentsThrowIllegalArgumentException() {
        Portfolio portfolio = new Portfolio(1000);
        Share share = new Share("AAA", "Share", 100);

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.buyAsset(null, 1));

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.buyAsset(share, 0));

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.sellAssetFIFO(null, 1, 100));

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.sellAssetFIFO("AAA", 0, 100));

        assertThrows(IllegalArgumentException.class,
                () -> portfolio.sellAssetFIFO("AAA", 1, -10));
    }
}
package com.stockmarket;

import com.stockmarket.domain.Share;
import com.stockmarket.logic.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioPersistenceConsistencyTest {

    @TempDir
    Path tempDir;

    @Test
    void throwsExceptionWhenDeclaredQuantityDoesNotMatchSumOfLots() throws IOException {
        Path file = tempDir.resolve("broken.txt");

        Files.writeString(file,
                "HEADER|CASH|1000\n" +
                        "ASSET|SHARE|AAA|Share|100|10\n" +
                        "LOT|2023-01-01|5|100\n"
        );

        PortfolioFileReader reader = new PortfolioFileReader();

        assertThrows(
                DataIntegrityException.class,
                () -> reader.load(file),
                "Reader must reject file when declared quantity does not match sum of LOT quantities"
        );
    }

    @Test
    void saveThenLoad_preservesCashPositionsAndLots() throws IOException {
        Portfolio original = new Portfolio(1000);

        Share share = new Share("AAA", "Share", 100);
        original.putPositionForLoad(share);

        AssetPosition pos = original.getPositionBySymbol("AAA");
        assertNotNull(pos);

        pos.addLot(new PurchaseLot(LocalDate.of(2023, 1, 1), 2, 100));
        pos.addLot(new PurchaseLot(LocalDate.of(2023, 1, 2), 3, 110));

        Path file = tempDir.resolve("roundtrip.txt");
        new PortfolioFileWriter().save(original, file);

        Portfolio loaded = new PortfolioFileReader().load(file);

        assertEquals(original.getCash(), loaded.getCash(), 0.0001);

        AssetPosition loadedPos = loaded.getPositionBySymbol("AAA");
        assertNotNull(loadedPos);

        assertEquals(5, loadedPos.getTotalQuantity());

        int sumQty = 0;
        int lotCount = 0;
        for (PurchaseLot lot : loadedPos.getLots()) {
            sumQty += lot.getQuantity();
            lotCount++;
        }

        assertEquals(5, sumQty, "Sum of LOT quantities must match declared total quantity");
        assertEquals(2, lotCount, "Expected two lots after round-trip");
    }
}
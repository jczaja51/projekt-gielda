package com.stockmarket;

import com.stockmarket.domain.*;
import com.stockmarket.logic.AssetPosition;
import com.stockmarket.logic.DataIntegrityException;
import com.stockmarket.logic.Portfolio;
import com.stockmarket.logic.PortfolioFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioFileReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void load_correctFile_createsPortfolioWithAllAssets() throws IOException {
        Path file = tempDir.resolve("portfolio.txt");

        Files.writeString(file,
                "HEADER|CASH|1000\n" +
                        "ASSET|SHARE|AAA|Share|100|2\n" +
                        "LOT|2023-01-01|1|100\n" +
                        "LOT|2023-01-02|1|100\n" +
                        "ASSET|CURRENCY|USD|Dollar|100|3|1\n" +
                        "LOT|2023-01-01|3|100\n" +
                        "ASSET|COMMODITY|GLD|Gold|100|4|2\n" +
                        "LOT|2023-01-01|4|100\n"
        );

        PortfolioFileReader reader = new PortfolioFileReader();
        Portfolio portfolio = reader.load(file);

        assertNotNull(portfolio);
        assertEquals(1000.0, portfolio.getCash());

        AssetPosition sharePos = findPosition(portfolio, "AAA", Share.class);
        AssetPosition currencyPos = findPosition(portfolio, "USD", Currency.class);
        AssetPosition commodityPos = findPosition(portfolio, "GLD", Commodity.class);

        assertEquals(2, sharePos.getTotalQuantity());
        assertEquals(3, currencyPos.getTotalQuantity());
        assertEquals(4, commodityPos.getTotalQuantity());
    }

    @Test
    void load_missingHeader_throwsException() throws IOException {
        Path file = tempDir.resolve("broken.txt");

        Files.writeString(file,
                "ASSET|SHARE|AAA|Share|100|1\n" +
                        "LOT|2023-01-01|1|100\n"
        );

        PortfolioFileReader reader = new PortfolioFileReader();

        assertThrows(
                DataIntegrityException.class,
                () -> reader.load(file)
        );
    }

    @Test
    void load_declaredQuantityDoesNotMatchLots_throwsDataIntegrityException() throws IOException {
        Path file = tempDir.resolve("broken.txt");

        Files.writeString(file,
                "HEADER|CASH|1000\n" +
                        "ASSET|SHARE|AAA|Share|100|10\n" +
                        "LOT|2023-01-01|5|100\n"
        );

        PortfolioFileReader reader = new PortfolioFileReader();

        assertThrows(
                DataIntegrityException.class,
                () -> reader.load(file)
        );
    }

    private AssetPosition findPosition(
            Portfolio portfolio,
            String symbol,
            Class<? extends Asset> type
    ) {
        for (AssetPosition pos : portfolio.getPositions()) {
            Asset asset = pos.getAsset();
            if (asset.getSymbol().equals(symbol) && type.isInstance(asset)) {
                return pos;
            }
        }
        fail("Nie znaleziono pozycji: " + symbol);
        return null;
    }
}
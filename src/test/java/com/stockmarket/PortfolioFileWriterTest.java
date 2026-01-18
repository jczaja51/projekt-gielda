package com.stockmarket;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;
import com.stockmarket.logic.PortfolioFileWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioFileWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void save_nullPortfolio_throwsException() {
        PortfolioFileWriter writer = new PortfolioFileWriter();

        assertThrows(
                IllegalArgumentException.class,
                () -> writer.save(null, tempDir.resolve("x.txt"))
        );
    }

    @Test
    void save_nullPath_throwsException() {
        Portfolio portfolio = new Portfolio(1000);
        PortfolioFileWriter writer = new PortfolioFileWriter();

        assertThrows(
                IllegalArgumentException.class,
                () -> writer.save(portfolio, null)
        );
    }

    @Test
    void save_emptyPortfolio_writesOnlyHeader() throws IOException {
        Portfolio portfolio = new Portfolio(500);

        Path file = tempDir.resolve("empty.txt");
        new PortfolioFileWriter().save(portfolio, file);

        List<String> lines = Files.readAllLines(file);

        assertEquals(1, lines.size());
        assertEquals(
                "HEADER|CASH|" + portfolio.getCash(),
                lines.get(0)
        );
    }

    @Test
    void save_writesShareCorrectly() throws IOException {
        Portfolio portfolio = new Portfolio(1000);
        Asset share = new Share("AAA", "Share", 100);

        portfolio.buyAsset(share, 3);

        Path file = tempDir.resolve("share.txt");
        new PortfolioFileWriter().save(portfolio, file);

        List<String> lines = Files.readAllLines(file);

        boolean found = false;
        for (String line : lines) {
            if (line.startsWith("ASSET|SHARE|AAA|Share|100.0|3")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected SHARE asset line not found");
    }

    @Test
    void save_writesCurrencyCorrectly() throws IOException {
        Portfolio portfolio = new Portfolio(1000);
        Currency currency = new Currency("USD", "Dollar", 100, 1);

        portfolio.buyAsset(currency, 2);

        Path file = tempDir.resolve("currency.txt");
        new PortfolioFileWriter().save(portfolio, file);

        List<String> lines = Files.readAllLines(file);

        boolean found = false;
        for (String line : lines) {
            if (line.startsWith("ASSET|CURRENCY|USD|Dollar|100.0|2|1.0")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected CURRENCY asset line not found");
    }

    @Test
    void save_writesCommodityCorrectly() throws IOException {
        Portfolio portfolio = new Portfolio(1000);
        Commodity gold = new Commodity("GLD", "Gold", 100, 2);

        portfolio.buyAsset(gold, 4);

        Path file = tempDir.resolve("commodity.txt");
        new PortfolioFileWriter().save(portfolio, file);

        List<String> lines = Files.readAllLines(file);

        boolean found = false;
        for (String line : lines) {
            if (line.startsWith("ASSET|COMMODITY|GLD|Gold|100.0|4|2.0")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected COMMODITY asset line not found");
    }

    @Test
    void save_writesLotsInFifoOrder() throws IOException {
        Portfolio portfolio = new Portfolio(10_000);
        Asset share = new Share("AAA", "Share", 100);

        portfolio.buyAsset(share, 1);
        portfolio.buyAsset(share, 1);

        Path file = tempDir.resolve("fifo.txt");
        new PortfolioFileWriter().save(portfolio, file);

        List<String> allLines = Files.readAllLines(file);
        List<String> lotLines = new ArrayList<>();

        for (String line : allLines) {
            if (line.startsWith("LOT|")) {
                lotLines.add(line);
            }
        }

        assertEquals(2, lotLines.size());

        String firstDate = lotLines.get(0).split("\\|")[1];
        String secondDate = lotLines.get(1).split("\\|")[1];

        assertTrue(
                firstDate.compareTo(secondDate) <= 0,
                "LOT lines should be written in FIFO (date) order"
        );
    }

    @Test
    void save_throwsExceptionForUnknownAssetType() {
        Portfolio portfolio = new Portfolio(1000);

        Asset unknown = new Asset("XXX", "Unknown", 10) {
            @Override
            public double calculateRealValue(int q) {
                return 0;
            }

            @Override
            public double calculatePurchaseCost(int q) {
                return 0;
            }
        };

        portfolio.buyAsset(unknown, 1);

        PortfolioFileWriter writer = new PortfolioFileWriter();

        assertThrows(
                IllegalStateException.class,
                () -> writer.save(portfolio, tempDir.resolve("unknown.txt"))
        );
    }
}
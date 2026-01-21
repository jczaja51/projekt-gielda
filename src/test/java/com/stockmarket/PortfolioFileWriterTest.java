package com.stockmarket;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.Share;
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
}
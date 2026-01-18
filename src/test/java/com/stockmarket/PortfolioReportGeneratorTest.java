package com.stockmarket;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;
import com.stockmarket.logic.PortfolioReportGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioReportGeneratorTest {

    @Test
    void generateReport_nullPortfolio_throwsException() {
        PortfolioReportGenerator gen = new PortfolioReportGenerator();

        assertThrows(
                IllegalArgumentException.class,
                () -> gen.generateReport(null)
        );
    }

    @Test
    void generateReport_containsHeaderCashAndTotalValue() {
        Portfolio portfolio = new Portfolio(10_000);

        Asset share = new Share("AAA", "Share", 100);
        portfolio.buyAsset(share, 1);

        PortfolioReportGenerator gen = new PortfolioReportGenerator();
        String report = gen.generateReport(portfolio);

        assertTrue(report.contains("=== RAPORT PORTFELA ==="));
        assertTrue(report.contains("Gotówka: " + portfolio.getCash()));
        assertTrue(report.contains("Łączna wartość portfela: " + portfolio.calculateTotalValue()));
    }

    @Test
    void generateReport_includesAllAssetGroupsAndUsesPolymorphicValuation() {
        Portfolio portfolio = new Portfolio(10_000);

        Share share = new Share("AAA", "Share", 100);
        Currency currency = new Currency("USD", "Dollar", 100, 1);
        Commodity commodity = new Commodity("GLD", "Gold", 100, 2);

        int q = 10;

        portfolio.buyAsset(share, q);
        portfolio.buyAsset(currency, q);
        portfolio.buyAsset(commodity, q);

        PortfolioReportGenerator gen = new PortfolioReportGenerator();
        String report = gen.generateReport(portfolio);

        assertTrue(report.contains("SHARE | AAA | ilość: " + q + " | wartość: "));
        assertTrue(report.contains("CURRENCY | USD | ilość: " + q + " | wartość: "));
        assertTrue(report.contains("COMMODITY | GLD | ilość: " + q + " | wartość: "));

        double shareValue = share.calculateRealValue(q);
        double currencyValue = currency.calculateRealValue(q);
        double commodityValue = commodity.calculateRealValue(q);

        assertTrue(report.contains("SHARE | AAA | ilość: " + q + " | wartość: " + shareValue));
        assertTrue(report.contains("CURRENCY | USD | ilość: " + q + " | wartość: " + currencyValue));
        assertTrue(report.contains("COMMODITY | GLD | ilość: " + q + " | wartość: " + commodityValue));
    }

    @Test
    void generateReport_whenPortfolioHasNoPositions_stillBuildsValidReport() {
        Portfolio portfolio = new Portfolio(1234.5);

        PortfolioReportGenerator gen = new PortfolioReportGenerator();
        String report = gen.generateReport(portfolio);

        assertTrue(report.contains("=== RAPORT PORTFELA ==="));
        assertTrue(report.contains("Gotówka: " + portfolio.getCash()));
        assertTrue(report.contains("Łączna wartość portfela: " + portfolio.calculateTotalValue()));

        assertFalse(report.contains("SHARE |"));
        assertFalse(report.contains("CURRENCY |"));
        assertFalse(report.contains("COMMODITY |"));
    }
}
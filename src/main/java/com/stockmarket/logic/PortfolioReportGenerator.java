package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PortfolioReportGenerator {

    private final Comparator<AssetPosition> reportComparator =
            new Comparator<AssetPosition>() {
                @Override
                public int compare(AssetPosition a, AssetPosition b) {
                    double aValue = a.getAsset().calculateRealValue(a.getTotalQuantity());
                    double bValue = b.getAsset().calculateRealValue(b.getTotalQuantity());

                    int valueCmp = Double.compare(bValue, aValue);
                    if (valueCmp != 0) return valueCmp;

                    return a.getAsset().getSymbol().compareTo(b.getAsset().getSymbol());
                }
            };

    public String generateReport(Portfolio portfolio) {
        if (portfolio == null) {
            throw new IllegalArgumentException("Portfolio null.");
        }

        List<AssetPosition> shares = new ArrayList<>();
        List<AssetPosition> currencies = new ArrayList<>();
        List<AssetPosition> commodities = new ArrayList<>();

        for (AssetPosition pos : portfolio.getPositions()) {
            Asset asset = pos.getAsset();

            if (asset instanceof Share) {
                shares.add(pos);
            } else if (asset instanceof Currency) {
                currencies.add(pos);
            } else if (asset instanceof Commodity) {
                commodities.add(pos);
            }
        }

        shares.sort(reportComparator);
        currencies.sort(reportComparator);
        commodities.sort(reportComparator);

        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPORT PORTFELA ===\n");
        sb.append("Gotówka: ").append(portfolio.getCash()).append("\n\n");

        appendGroup(sb, "SHARE", shares);
        appendGroup(sb, "CURRENCY", currencies);
        appendGroup(sb, "COMMODITY", commodities);

        sb.append("\nŁączna wartość portfela: ")
                .append(portfolio.calculateTotalValue());

        return sb.toString();
    }

    private void appendGroup(StringBuilder sb, String label, List<AssetPosition> positions) {
        sb.append("-- ").append(label).append(" --\n");

        for (AssetPosition pos : positions) {
            int qty = pos.getTotalQuantity();
            double value = pos.getAsset().calculateRealValue(qty);

            sb.append(label)
                    .append(" | ")
                    .append(pos.getAsset().getSymbol())
                    .append(" | ilość: ")
                    .append(qty)
                    .append(" | wartość: ")
                    .append(value)
                    .append("\n");
        }

        sb.append("\n");
    }
}
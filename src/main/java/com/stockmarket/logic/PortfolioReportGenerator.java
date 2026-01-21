package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PortfolioReportGenerator {

    private static final Comparator<AssetPosition> REPORT_COMPARATOR =
            (a, b) -> {
                double va = a.getAsset().calculateRealValue(a.getTotalQuantity());
                double vb = b.getAsset().calculateRealValue(b.getTotalQuantity());

                int cmp = Double.compare(vb, va);
                if (cmp != 0) return cmp;

                return a.getAsset().getSymbol()
                        .compareTo(b.getAsset().getSymbol());
            };

    public String generateReport(Portfolio portfolio) {
        if (portfolio == null) {
            throw new IllegalArgumentException("Portfolio null.");
        }

        List<AssetPosition> positions = new ArrayList<>();
        for (AssetPosition pos : portfolio.getPositions()) {
            positions.add(pos);
        }

        positions.sort(REPORT_COMPARATOR);

        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPORT PORTFELA ===\n");

        for (AssetPosition pos : positions) {
            sb.append(pos.getAsset().getSymbol())
                    .append(" | ilość: ")
                    .append(pos.getTotalQuantity())
                    .append(" | wartość: ")
                    .append(pos.getAsset()
                            .calculateRealValue(pos.getTotalQuantity()))
                    .append("\n");
        }

        return sb.toString();
    }
}
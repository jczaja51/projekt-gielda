package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class PortfolioFileReader {

    public Portfolio load(Path path) throws IOException {
        Portfolio portfolio = null;
        Asset currentAsset = null;
        int declaredQty = 0;
        int lotSum = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|");

                switch (p[0]) {

                    case "HEADER" -> {
                        if (p.length != 3 || !"CASH".equals(p[1]))
                            throw new DataIntegrityException("Błędny HEADER");

                        portfolio = new Portfolio(Double.parseDouble(p[2]));
                    }

                    case "ASSET" -> {
                        if (portfolio == null)
                            throw new DataIntegrityException("ASSET przed HEADER");

                        if (declaredQty != lotSum && currentAsset != null)
                            throw new DataIntegrityException("Niespójność LOT");

                        AssetType type = AssetType.valueOf(p[1]);
                        String symbol = p[2];
                        String name = p[3];
                        double price = Double.parseDouble(p[4]);
                        declaredQty = Integer.parseInt(p[5]);
                        lotSum = 0;

                        currentAsset = switch (type) {
                            case SHARE -> new Share(symbol, name, price);
                            case CURRENCY -> new Currency(symbol, name, price, Double.parseDouble(p[6]));
                            case COMMODITY -> new Commodity(symbol, name, price, Double.parseDouble(p[6]));
                        };

                        portfolio.putPositionForLoad(currentAsset);
                    }

                    case "LOT" -> {
                        if (currentAsset == null)
                            throw new DataIntegrityException("LOT bez ASSET");

                        int qty = Integer.parseInt(p[2]);
                        lotSum += qty;

                        portfolio.getPositionBySymbol(currentAsset.getSymbol())
                                .addLot(new PurchaseLot(
                                        LocalDate.parse(p[1]),
                                        qty,
                                        Double.parseDouble(p[3])
                                ));
                    }

                    default -> throw new DataIntegrityException("Nieznany rekord");
                }
            }
        }

        if (portfolio == null)
            throw new DataIntegrityException("Brak HEADER");

        if (declaredQty != lotSum)
            throw new DataIntegrityException("Niespójność LOT");

        return portfolio;
    }
}
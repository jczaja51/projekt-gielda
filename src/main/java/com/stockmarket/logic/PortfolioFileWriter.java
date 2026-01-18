package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PortfolioFileWriter {

    public void save(Portfolio portfolio, Path path) throws IOException {
        if (portfolio == null) {
            throw new IllegalArgumentException("Portfolio nie może być null.");
        }
        if (path == null) {
            throw new IllegalArgumentException("Ścieżka nie może być null.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            writer.write("HEADER|CASH|" + portfolio.getCash());
            writer.newLine();

            for (AssetPosition position : portfolio.getPositions()) {

                Asset asset = position.getAsset();
                if (asset == null) {
                    throw new DataIntegrityException("Pozycja zawiera null Asset.");
                }

                int declaredQuantity = position.getTotalQuantity();
                if (declaredQuantity < 0) {
                    throw new DataIntegrityException("Ujemna ilość w pozycji: " + asset.getSymbol());
                }

                writeAssetLine(writer, asset, declaredQuantity);
                writer.newLine();

                List<PurchaseLot> fifoLots = new ArrayList<>(position.getLots());
                fifoLots.sort(Comparator.comparing(PurchaseLot::getPurchaseDate));

                for (PurchaseLot lot : fifoLots) {
                    if (lot == null) {
                        throw new DataIntegrityException("Null LOT w pozycji: " + asset.getSymbol());
                    }
                    if (lot.getQuantity() <= 0) {
                        continue;
                    }

                    writer.write("LOT|" +
                            lot.getPurchaseDate() + "|" +
                            lot.getQuantity() + "|" +
                            lot.getUnitPrice());
                    writer.newLine();
                }
            }
        }
    }

    private void writeAssetLine(BufferedWriter writer, Asset asset, int declaredQuantity) throws IOException {
        if (asset instanceof Share) {
            writer.write("ASSET|SHARE|" +
                    asset.getSymbol() + "|" +
                    asset.getName() + "|" +
                    asset.getBasePrice() + "|" +
                    declaredQuantity);
            return;
        }

        if (asset instanceof Currency) {
            Currency c = (Currency) asset;
            writer.write("ASSET|CURRENCY|" +
                    c.getSymbol() + "|" +
                    c.getName() + "|" +
                    c.getBasePrice() + "|" +
                    declaredQuantity + "|" +
                    c.getSpread());
            return;
        }

        if (asset instanceof Commodity) {
            Commodity c = (Commodity) asset;
            writer.write("ASSET|COMMODITY|" +
                    c.getSymbol() + "|" +
                    c.getName() + "|" +
                    c.getBasePrice() + "|" +
                    declaredQuantity + "|" +
                    c.getStorageCostPerUnitPerDay());
            return;
        }

        throw new IllegalStateException("Nieznany typ aktywa: " + asset.getClass().getName());
    }
}
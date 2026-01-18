package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class PortfolioFileReader {

    public Portfolio load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Ścieżka nie może być null.");
        }

        Portfolio portfolio = null;
        Asset currentAsset = null;

        Integer declaredQuantity = null;
        int lotsQuantitySum = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length == 0 || parts[0].isBlank()) {
                    throw new DataIntegrityException("Linia " + lineNo + ": brak typu rekordu.");
                }

                RecordType recordType = parseRecordType(parts[0], lineNo);

                switch (recordType) {

                    case HEADER -> {
                        if (parts.length != 3) {
                            throw new DataIntegrityException("Linia " + lineNo + ": Niepoprawny HEADER (zła liczba pól).");
                        }
                        if (!"CASH".equals(parts[1])) {
                            throw new DataIntegrityException("Linia " + lineNo + ": Niepoprawny HEADER (oczekiwano CASH).");
                        }

                        double cash = parseDouble(parts[2], "Linia " + lineNo + ": Niepoprawna wartość CASH.");

                        portfolio = new Portfolio(cash);
                        currentAsset = null;
                        declaredQuantity = null;
                        lotsQuantitySum = 0;
                    }

                    case ASSET -> {
                        if (portfolio == null) {
                            throw new DataIntegrityException("Linia " + lineNo + ": ASSET przed HEADER.");
                        }

                        validateLotsConsistency(declaredQuantity, lotsQuantitySum);

                        if (parts.length < 6) {
                            throw new DataIntegrityException("Linia " + lineNo + ": Niepoprawna linia ASSET (za mało pól).");
                        }

                        AssetType type = parseAssetType(parts[1], lineNo);
                        String symbol = requireNonBlank(parts[2], "Linia " + lineNo + ": Symbol ASSET nie może być pusty.");
                        String name = requireNonBlank(parts[3], "Linia " + lineNo + ": Nazwa ASSET nie może być pusta.");

                        double basePrice = parseDouble(parts[4], "Linia " + lineNo + ": Niepoprawna cena bazowa.");
                        declaredQuantity = parseInt(parts[5], "Linia " + lineNo + ": Niepoprawna declaredQuantity.");
                        lotsQuantitySum = 0;

                        currentAsset = switch (type) {
                            case SHARE -> {
                                if (parts.length != 6) {
                                    throw new DataIntegrityException("Linia " + lineNo + ": Niepoprawna linia SHARE (oczekiwano 6 pól).");
                                }
                                yield new Share(symbol, name, basePrice);
                            }
                            case CURRENCY -> {
                                if (parts.length != 7) {
                                    throw new DataIntegrityException("Linia " + lineNo + ": Niepoprawna linia CURRENCY (oczekiwano 7 pól).");
                                }
                                double spread = parseDouble(parts[6], "Linia " + lineNo + ": Niepoprawny spread.");
                                yield new Currency(symbol, name, basePrice, spread);
                            }
                            case COMMODITY -> {
                                if (parts.length != 7) {
                                    throw new DataIntegrityException("Linia " + lineNo + ": Niepoprawna linia COMMODITY (oczekiwano 7 pól).");
                                }
                                double storage = parseDouble(parts[6], "Linia " + lineNo + ": Niepoprawny koszt składowania.");
                                yield new Commodity(symbol, name, basePrice, storage);
                            }
                        };

                        portfolio.putPositionForLoad(currentAsset);
                    }

                    case LOT -> {
                        if (currentAsset == null) {
                            throw new DataIntegrityException("Linia " + lineNo + ": LOT bez ASSET.");
                        }

                        if (parts.length != 4) {
                            throw new DataIntegrityException("Linia " + lineNo + ": Niepoprawna linia LOT (oczekiwano 4 pól).");
                        }

                        LocalDate date = parseDate(parts[1], "Linia " + lineNo + ": Niepoprawna data LOT.");
                        int qty = parseInt(parts[2], "Linia " + lineNo + ": Niepoprawna ilość LOT.");
                        double price = parseDouble(parts[3], "Linia " + lineNo + ": Niepoprawna cena LOT.");

                        lotsQuantitySum += qty;

                        AssetPosition pos = portfolio.getPositionBySymbol(currentAsset.getSymbol());
                        if (pos == null) {
                            throw new DataIntegrityException("Linia " + lineNo + ": Brak pozycji dla symbolu " + currentAsset.getSymbol());
                        }

                        pos.addLot(new PurchaseLot(date, qty, price));
                    }
                }
            }
        }

        validateLotsConsistency(declaredQuantity, lotsQuantitySum);

        if (portfolio == null) {
            throw new DataIntegrityException("Brak HEADER.");
        }

        return portfolio;
    }

    private RecordType parseRecordType(String s, int lineNo) {
        try {
            return RecordType.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new DataIntegrityException("Linia " + lineNo + ": Nieznany rekord: " + s, e);
        }
    }

    private AssetType parseAssetType(String s, int lineNo) {
        try {
            return AssetType.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new DataIntegrityException("Linia " + lineNo + ": Nieznany AssetType: " + s, e);
        }
    }

    private LocalDate parseDate(String s, String msg) {
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            throw new DataIntegrityException(msg, e);
        }
    }

    private void validateLotsConsistency(Integer declared, int sum) {
        if (declared != null && declared != sum) {
            throw new DataIntegrityException(
                    "Niespójność danych: declaredQuantity=" + declared + ", suma LOT=" + sum
            );
        }
    }

    private double parseDouble(String s, String msg) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new DataIntegrityException(msg, e);
        }
    }

    private int parseInt(String s, String msg) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new DataIntegrityException(msg, e);
        }
    }

    private String requireNonBlank(String s, String msg) {
        if (s == null || s.isBlank()) {
            throw new DataIntegrityException(msg);
        }
        return s.trim();
    }
}
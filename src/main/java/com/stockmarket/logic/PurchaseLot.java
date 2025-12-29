package com.stockmarket.logic;

import java.time.LocalDate;

public class PurchaseLot {
    private final LocalDate purchaseDate;
    private int quantity;
    private final double unitPrice;

    public PurchaseLot(LocalDate purchaseDate, int quantity, double unitPrice) {
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Data zakupu nie może być null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ilość w partii musi być dodatnia.");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Cena jednostkowa musi być > 0.");
        }
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Zmniejszenie musi być dodatnie.");
        if (amount > quantity) throw new IllegalArgumentException("Nie można zmniejszyć poniżej zera.");
        quantity -= amount;
    }
}
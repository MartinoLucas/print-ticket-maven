package org.example.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Item {
    private final String sku;
    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal taxRate; // ej 0.21 para 21%

    public Item(String sku, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal taxRate) {
        this.sku = sku;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
    }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTaxRate() { return taxRate; }
    // Total del renglón CON IVA incluido (precio final * cantidad)
    public BigDecimal lineTotalWithIva() {
        return unitPrice.multiply(quantity);
    }

    // Neto del renglón (desglosado desde el total con IVA)
    public BigDecimal lineNet() {
        return lineTotalWithIva()
                .divide(BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP);
    }

    // IVA del renglón (total con IVA - neto)
    public BigDecimal lineTax() {
        return lineTotalWithIva().subtract(lineNet());
    }

    // Total del renglón (con IVA) — para compatibilidad
    public BigDecimal lineTotal() {
        return lineTotalWithIva();
    }

}

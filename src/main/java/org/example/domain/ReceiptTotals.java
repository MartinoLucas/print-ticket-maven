package org.example.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ReceiptTotals {
    private final BigDecimal net;             // Base imponible (sin IVA)
    private final BigDecimal tax;             // IVA total
    private final BigDecimal subtotal;        // Total con IVA antes de descuento
    private final Map<String, BigDecimal> taxBreakdown;
    private final String discountLabel;
    private final BigDecimal discountAmount;  // descuento aplicado en pesos
    private final BigDecimal finalTotal;      // total después del descuento

    public ReceiptTotals(BigDecimal net,
                         BigDecimal tax,
                         BigDecimal subtotal,
                         Map<String, BigDecimal> taxBreakdown) {
        this(net, tax, subtotal, taxBreakdown, "", BigDecimal.ZERO);
    }

    public ReceiptTotals(BigDecimal net,
                         BigDecimal tax,
                         BigDecimal subtotal,
                         Map<String, BigDecimal> taxBreakdown,
                         String discountLabel,
                         BigDecimal discountAmount) {
        this.net = net.setScale(2, RoundingMode.HALF_UP);
        this.tax = tax.setScale(2, RoundingMode.HALF_UP);
        this.subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        this.taxBreakdown = taxBreakdown;
        this.discountLabel = discountLabel;
        this.discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        this.finalTotal = this.subtotal.subtract(this.discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getNet() { return net; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getSubtotal() { return subtotal; }
    public Map<String, BigDecimal> getTaxBreakdown() { return taxBreakdown; }

    public String getDiscountLabel() { return discountLabel; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getFinalTotal() { return finalTotal; }
}

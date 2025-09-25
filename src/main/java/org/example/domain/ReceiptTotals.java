package org.example.domain;

import java.math.BigDecimal;
import java.util.Map;

public class ReceiptTotals {
    private final BigDecimal net;
    private final BigDecimal tax;
    private final BigDecimal total;
    private final Map<String, BigDecimal> taxBreakdown;
    private final String discountLabel;

    // 🔹 Nuevos campos
    private final BigDecimal discountAmount;   // descuento aplicado en pesos
    private final BigDecimal finalTotal;       // total después del descuento

    public ReceiptTotals(BigDecimal net,
                         BigDecimal tax,
                         BigDecimal total,
                         Map<String, BigDecimal> taxBreakdown) {
        this(net, tax, total, taxBreakdown, "", BigDecimal.ZERO);
    }

    public ReceiptTotals(BigDecimal net,
                         BigDecimal tax,
                         BigDecimal total,
                         Map<String, BigDecimal> taxBreakdown, String discountLabel,
                         BigDecimal discountAmount) {
        this.net = net;
        this.tax = tax;
        this.total = total;
        this.taxBreakdown = taxBreakdown;
        this.discountLabel = discountLabel;
        this.discountAmount = discountAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.finalTotal = total.subtract(this.discountAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getNet() { return net; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotal() { return total; }
    public Map<String, BigDecimal> getTaxBreakdown() { return taxBreakdown; }

    // 🔹 Nuevos getters
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getFinalTotal() { return finalTotal; }

    public String getDiscountLabel() { return discountLabel; }
}

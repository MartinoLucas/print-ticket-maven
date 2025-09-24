package org.example.domain;

import java.math.BigDecimal;
import java.util.Map;

public class ReceiptTotals {
    private final BigDecimal net;
    private final BigDecimal tax;
    private final BigDecimal total;
    private final Map<String, BigDecimal> taxBreakdown; // "IVA 21%" -> amount

    public ReceiptTotals(BigDecimal net, BigDecimal tax, BigDecimal total, Map<String, BigDecimal> taxBreakdown) {
        this.net = net; this.tax = tax; this.total = total; this.taxBreakdown = taxBreakdown;
    }
    public BigDecimal getNet() { return net; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotal() { return total; }
    public Map<String, BigDecimal> getTaxBreakdown() { return taxBreakdown; }
}

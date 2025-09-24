package org.example.service;

import org.example.domain.Item;
import org.example.domain.ReceiptTotals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TotalsCalculator {
    public ReceiptTotals compute(List<Item> items) {
        BigDecimal net = BigDecimal.ZERO, tax = BigDecimal.ZERO;
        Map<String, BigDecimal> br = new LinkedHashMap<>();
        for (Item it : items) {
            net = net.add(it.lineNet());
            BigDecimal lineTax = it.lineTax();
            tax = tax.add(lineTax);

            String key = "IVA " + it.getTaxRate().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP) + "%";
            br.put(key, br.getOrDefault(key, BigDecimal.ZERO).add(lineTax));
        }
        BigDecimal total = net.add(tax).setScale(2, RoundingMode.HALF_UP);
        return new ReceiptTotals(net.setScale(2, RoundingMode.HALF_UP), tax.setScale(2, RoundingMode.HALF_UP), total, br);
    }
}

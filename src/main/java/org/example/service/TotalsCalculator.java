package org.example.service;

import org.example.domain.Item;
import org.example.domain.ReceiptRequest;
import org.example.domain.ReceiptTotals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TotalsCalculator {
    public ReceiptTotals compute(ReceiptRequest req) {
        List<Item> items = req.getItems();

        BigDecimal net = BigDecimal.ZERO, tax = BigDecimal.ZERO;
        Map<String, BigDecimal> br = new LinkedHashMap<>();

        for (Item it : items) {
            net = net.add(it.lineNet());
            BigDecimal lineTax = it.lineTax();
            tax = tax.add(lineTax);

            String key = "IVA " + it.getTaxRate()
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP) + "%";
            br.put(key, br.getOrDefault(key, BigDecimal.ZERO).add(lineTax));
        }

        BigDecimal total = items.stream()
                .map(Item::lineTotalWithIva)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // 🔹 Calcular descuento
        BigDecimal discount = BigDecimal.ZERO;
        String discountLabel = "";

        // Descuento fijo ($)
        if (req.getDiscountValue() != null && req.getDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
            discount = discount.add(req.getDiscountValue());
            discountLabel = "-$" + req.getDiscountValue().setScale(2, RoundingMode.HALF_UP);
        }

        // Descuento en porcentaje (%)
        if (req.getDiscountPercent() != null && req.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentDiscount = total.multiply(req.getDiscountPercent())
                    .setScale(2, RoundingMode.HALF_UP);
            discount = discount.add(percentDiscount);
            discountLabel = "-" + req.getDiscountPercent().multiply(BigDecimal.valueOf(100)).setScale(0) + "%";
        }

        // Nunca mayor que el total
        if (discount.compareTo(total) > 0) {
            discount = total;
        }

        return new ReceiptTotals(
                net.setScale(2, RoundingMode.HALF_UP),
                tax.setScale(2, RoundingMode.HALF_UP),
                total,
                br,
                discountLabel,
                discount
        );
    }
}

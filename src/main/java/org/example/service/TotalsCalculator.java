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

        BigDecimal net = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        Map<String, BigDecimal> br = new LinkedHashMap<>();

        // Calcular neto e IVA desglosado
        for (Item it : items) {
            net = net.add(it.lineNet());
            BigDecimal lineTax = it.lineTax();
            tax = tax.add(lineTax);

            String key = "IVA " + it.getTaxRate()
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP) + "%";
            br.put(key, br.getOrDefault(key, BigDecimal.ZERO).add(lineTax));
        }

        // Subtotal = suma de precios con IVA incluido
        BigDecimal subtotal = items.stream()
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
            BigDecimal percentDiscount = subtotal.multiply(req.getDiscountPercent())
                    .setScale(2, RoundingMode.HALF_UP);
            discount = discount.add(percentDiscount);
            discountLabel = "-" + req.getDiscountPercent().multiply(BigDecimal.valueOf(100)).setScale(0) + "%";
        }

        // Nunca mayor que el subtotal
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return new ReceiptTotals(
                net,
                tax,
                subtotal,
                br,
                discountLabel,
                discount
        );
    }
}

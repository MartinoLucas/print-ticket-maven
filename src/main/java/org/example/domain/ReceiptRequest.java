package org.example.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptRequest {
    private final String customerName;
    private final String customerDoc;
    private final String pvNumber;
    private final String invoiceNumber;
    private final LocalDateTime dateTime;
    private final List<Item> items;
    private final List<Payment> payments;

    // 🔹 Nuevos campos
    private final BigDecimal discountValue;   // valor fijo en pesos
    private final BigDecimal discountPercent; // porcentaje (ej 0.10 para 10%)

    public ReceiptRequest(String customerName,
                          String customerDoc,
                          String pvNumber,
                          String invoiceNumber,
                          LocalDateTime dateTime,
                          List<Item> items,
                          List<Payment> payments,
                          BigDecimal discountValue,
                          BigDecimal discountPercent) {
        this.customerName = customerName;
        this.customerDoc = customerDoc;
        this.pvNumber = pvNumber;
        this.invoiceNumber = invoiceNumber;
        this.dateTime = dateTime;
        this.items = items;
        this.payments = payments;
        this.discountValue = discountValue != null ? discountValue : BigDecimal.ZERO;
        this.discountPercent = discountPercent != null ? discountPercent : BigDecimal.ZERO;
    }

    public String getCustomerName() { return customerName; }
    public String getCustomerDoc() { return customerDoc; }
    public String getPvNumber() { return pvNumber; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDateTime getDateTime() { return dateTime; }
    public List<Item> getItems() { return items; }
    public List<Payment> getPayments() { return payments; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
}

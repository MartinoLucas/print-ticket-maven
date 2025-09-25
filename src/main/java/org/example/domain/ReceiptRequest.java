package org.example.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptRequest {
    private final String customerName;
    private final String customerDoc;
    private final String customerAddress;   // 🆕 Dirección del cliente
    private final String customerIvaCondition; // 🆕 Condición frente al IVA (ej: "Consumidor Final", "IVA Responsable Inscripto")

    private final String pvNumber;          // Punto de venta
    private final String invoiceNumber;     // Número de factura
    private final LocalDateTime dateTime;   // Fecha y hora de emisión

    private final List<Item> items;
    private final List<Payment> payments;

    // 🔹 Descuentos
    private final BigDecimal discountValue;   // valor fijo en pesos
    private final BigDecimal discountPercent; // porcentaje (ej 0.10 para 10%)

    public ReceiptRequest(String customerName,
                          String customerDoc,
                          String customerAddress,
                          String customerIvaCondition,
                          String pvNumber,
                          String invoiceNumber,
                          LocalDateTime dateTime,
                          List<Item> items,
                          List<Payment> payments,
                          BigDecimal discountValue,
                          BigDecimal discountPercent) {
        this.customerName = customerName;
        this.customerDoc = customerDoc;
        this.customerAddress = customerAddress != null ? customerAddress : "";
        this.customerIvaCondition = customerIvaCondition != null ? customerIvaCondition : "A CONSUMIDOR FINAL";

        this.pvNumber = pvNumber;
        this.invoiceNumber = invoiceNumber;
        this.dateTime = dateTime;
        this.items = items;
        this.payments = payments;

        this.discountValue = discountValue != null ? discountValue : BigDecimal.ZERO;
        this.discountPercent = discountPercent != null ? discountPercent : BigDecimal.ZERO;
    }

    // ===== Getters =====
    public String getCustomerName() { return customerName; }
    public String getCustomerDoc() { return customerDoc; }
    public String getCustomerAddress() { return customerAddress; }
    public String getCustomerIvaCondition() { return customerIvaCondition; }

    public String getPvNumber() { return pvNumber; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDateTime getDateTime() { return dateTime; }

    public List<Item> getItems() { return items; }
    public List<Payment> getPayments() { return payments; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
}

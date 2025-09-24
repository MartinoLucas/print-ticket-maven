package org.example.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ReceiptRequest {
    private final String customerName;   // "Consumidor final", etc.
    private final String customerDoc;    // DNI/CUIT si aplica
    private final String posNumber;      // PV
    private final String invoiceNumber;  // Nro comprobante
    private final LocalDateTime dateTime;
    private final List<Item> items;
    private final List<Payment> payments;

    public ReceiptRequest(String customerName, String customerDoc, String posNumber,
                          String invoiceNumber, LocalDateTime dateTime,
                          List<Item> items, List<Payment> payments) {
        this.customerName = customerName;
        this.customerDoc = customerDoc;
        this.posNumber = posNumber;
        this.invoiceNumber = invoiceNumber;
        this.dateTime = dateTime;
        this.items = items;
        this.payments = payments;
    }
    public String getCustomerName() { return customerName; }
    public String getCustomerDoc() { return customerDoc; }
    public String getPosNumber() { return posNumber; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDateTime getDateTime() { return dateTime; }
    public List<Item> getItems() { return items; }
    public List<Payment> getPayments() { return payments; }
}

package org.example.template;

import org.example.config.AppConfig;
import org.example.domain.ReceiptRequest;
import org.example.domain.ReceiptTotals;
import org.example.printer.EscPosCoffeePrinter;

public interface ReceiptTemplate {
    void print(EscPosCoffeePrinter printer, AppConfig cfg, ReceiptRequest req, ReceiptTotals totals) throws Exception;
}

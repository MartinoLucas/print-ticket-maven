package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.AppConfig;
import org.example.domain.Item;
import org.example.domain.Payment;
import org.example.domain.ReceiptRequest;
import org.example.domain.ReceiptTotals;
import org.example.printer.EscPosCoffeePrinter;
import org.example.printer.PrinterFactory;
import org.example.service.TotalsCalculator;
import org.example.template.AfipBTemplate;
import org.example.template.ReceiptTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/print")
public class PrintController {

    @PostMapping
    public String printTicket(@RequestBody ReceiptRequest request) {
        try {
            // 1) Configuración desde config.json
            ObjectMapper om = new ObjectMapper();
            AppConfig cfg = om.readValue(new File("config.json"), AppConfig.class);

            // 2) Calcular totales
            TotalsCalculator calc = new TotalsCalculator();
            ReceiptTotals totals = calc.compute(request);

            // 3) Inicializar impresora
            EscPosCoffeePrinter printer = PrinterFactory.create(cfg.printer);

            // 4) Template
            ReceiptTemplate tpl = new AfipBTemplate();
            tpl.print(printer, cfg, request, totals);

            // 5) Cerrar
            printer.close();

            return "✅ Ticket impreso correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error imprimiendo: " + e.getMessage();
        }
    }
}

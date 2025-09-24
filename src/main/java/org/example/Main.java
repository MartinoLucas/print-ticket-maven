package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.AppConfig;
import org.example.domain.*;
import org.example.printer.EscPosCoffeePrinter;
import org.example.printer.PrinterFactory;
import org.example.service.TotalsCalculator;
import org.example.template.AfipBTemplate;
import org.example.template.ReceiptTemplate;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // 1) Cargar config
            ObjectMapper om = new ObjectMapper();
            AppConfig cfg = om.readValue(new File("config.json"), AppConfig.class);

            // 2) Crear request (en real life viene del backend / JSON)
            List<Item> items = Arrays.asList(
                    new Item("779089500023", "GASEOSA COLA LATA x 355", bd(1), bd(1500), bd(0.21)),
                    new Item("401504211214", "CERVEZA DUNKEL 500cc",   bd(1), bd(1190), bd(0.21))
            );
            List<Payment> payments = List.of(new Payment(Payment.Method.CASH, bd(2690)));

            ReceiptRequest req = new ReceiptRequest(
                    "Consumidor final",
                    "", // DNI/CUIT si aplica
                    cfg.pvNumber,
                    "00001234",
                    LocalDateTime.now(),
                    items,
                    payments
            );

            // 3) Calcular totales
            TotalsCalculator calc = new TotalsCalculator();
            ReceiptTotals totals = calc.compute(items);

            // 4) Resolver impresora vía Factory (Port-Adapter)
            EscPosCoffeePrinter printer = PrinterFactory.create(cfg.printer);

            // 5) Elegir template (Strategy)
            ReceiptTemplate tpl = new AfipBTemplate();
            tpl.print(printer, cfg, req, totals);

            // 6) Cerrar
            printer.close();
            System.out.println("Ticket impreso.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BigDecimal bd(double v) { return BigDecimal.valueOf(v); }
}

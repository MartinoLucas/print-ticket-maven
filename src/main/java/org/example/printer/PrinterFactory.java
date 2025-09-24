package org.example.printer;

import com.github.anastaciocintra.output.PrinterOutputStream;
import org.example.config.AppConfig;

import javax.print.PrintService;

public class PrinterFactory {
    public static EscPosCoffeePrinter create(AppConfig.PrinterCfg cfg) throws Exception {
        PrintService ps;
        if ("NAMED".equalsIgnoreCase(cfg.mode) && cfg.name != null && !cfg.name.isEmpty()) {
            ps = PrinterOutputStream.getPrintServiceByName(cfg.name);
            if (ps == null) throw new IllegalStateException("Impresora no encontrada: " + cfg.name);
        } else {
            ps = PrinterOutputStream.getDefaultPrintService();
            if (ps == null) throw new IllegalStateException("No hay impresora predeterminada.");
        }
        return new EscPosCoffeePrinter(ps);
    }
}

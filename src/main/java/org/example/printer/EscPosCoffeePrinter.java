package org.example.printer;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;

import javax.print.PrintService;

public class EscPosCoffeePrinter implements Printer {
    private final PrinterOutputStream out;
    private final EscPos escpos;

    public EscPosCoffeePrinter(PrintService ps) throws Exception {
        this.out = new PrinterOutputStream(ps);
        this.escpos = new EscPos(out);
    }

    public EscPos escpos() { return escpos; } // expone API de alto nivel al template

    @Override
    public void write(byte[] data) throws Exception {
        String s = new String(data, "CP437"); // o UTF-8, según tu impresora
        escpos.write(s);
    }

    @Override public void close() throws Exception { escpos.close(); out.close(); }
}

package org.example.template;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.QRCode;

import org.example.config.AppConfig;
import org.example.domain.Item;
import org.example.domain.Payment;
import org.example.domain.ReceiptRequest;
import org.example.domain.ReceiptTotals;
import org.example.printer.EscPosCoffeePrinter;
import org.example.util.Columns;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import com.github.anastaciocintra.escpos.image.*;

import javax.imageio.ImageIO;


public class AfipBTemplate implements ReceiptTemplate {

    @Override
    public void print(EscPosCoffeePrinter p, AppConfig cfg, ReceiptRequest r, ReceiptTotals t) throws Exception {
        final int W = cfg.paper.widthChars;

        Style title = new Style().setBold(true)
                .setFontSize(Style.FontSize._2, Style.FontSize._2)
                .setJustification(EscPosConst.Justification.Center);

        Style left = new Style().setJustification(EscPosConst.Justification.Left_Default);
        Style right = new Style().setJustification(EscPosConst.Justification.Right);
        Style mid = new Style().setJustification(EscPosConst.Justification.Center);
        Style boldR = new Style().setJustification(EscPosConst.Justification.Right).setBold(true);

        // ===== Encabezado (logo opcional) =====
        if (cfg.logo != null && cfg.logo.path != null && !cfg.logo.path.isEmpty()) {
            try (InputStream is = getClass().getResourceAsStream(cfg.logo.path)) {
                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    int maxWidth = (cfg.logo.maxWidth > 0) ? cfg.logo.maxWidth : 200;
                    BufferedImage scaled = scaleImage(img, maxWidth);

                    CoffeeImageImpl coffeeImage = new CoffeeImageImpl(scaled);
                    BitonalThreshold bitonal = new BitonalThreshold();
                    EscPosImage escposImage = new EscPosImage(coffeeImage, bitonal);

                    // ✅ wrapper recomendado para evitar “comerse” el primer texto
                    RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
                    imageWrapper.setJustification(EscPosConst.Justification.Center);

                    p.escpos().write(imageWrapper, escposImage);

                    // ⚡ separá el bloque gráfico y “pateá” a modo texto
                    p.escpos().feed(2);
                    p.escpos().writeLF(new Style(), " ");  // línea en blanco en modo texto
                    p.escpos().setStyle(new Style());      // reset estilo
                }
            } catch (Exception ex) {
                System.err.println("No se pudo cargar logo: " + ex.getMessage());
            }
        }

// ===== Nombre del comercio =====
        p.escpos().writeLF(
                new Style().setBold(true)
                        .setFontSize(Style.FontSize._2, Style.FontSize._2)
                        .setJustification(EscPosConst.Justification.Center),
                cfg.storeName
        );

        p.escpos().writeLF(
                new Style().setJustification(EscPosConst.Justification.Center),
                cfg.address
        );

        p.escpos().writeLF(
                new Style().setJustification(EscPosConst.Justification.Center),
                "Tel: " + cfg.phone
        );
        p.escpos().writeLF(Columns.line(W, '-'));



        // Datos comprobante
        p.escpos().writeLF(left, cfg.invoiceLabel);
        String dt = r.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"));
        p.escpos().writeLF(left, "P.V.: " + cfg.pvNumber + "   N°: " + r.getInvoiceNumber());
        p.escpos().writeLF(left, "Fecha: " + dt);
        p.escpos().writeLF(Columns.line(W, '-'));

        // Cliente
        p.escpos().writeLF(left, r.getCustomerName());
        if (r.getCustomerDoc() != null && !r.getCustomerDoc().isEmpty())
            p.escpos().writeLF(left, r.getCustomerDoc());
        p.escpos().writeLF(Columns.line(W, '-'));

        // Ítems
        for (Item it : r.getItems()) {
            String l1 = it.getQuantity() + " x " + money(cfg, it.getUnitPrice()) + "  " + it.getDescription();
            p.escpos().writeLF(left, l1);
            String l2 = Columns.lr("", money(cfg, it.lineNet()), W);
            p.escpos().writeLF(right, l2);
            if (it.getSku() != null && !it.getSku().isEmpty())
                p.escpos().writeLF(left, it.getSku());
        }
        p.escpos().writeLF(Columns.line(W, '-'));

        // Subtotal/Total
        p.escpos().writeLF(boldR, Columns.lr("SUBTOTAL", money(cfg, t.getNet()), W));
        p.escpos().writeLF(boldR, Columns.lr("TOTAL",    money(cfg, t.getTotal()), W));
        p.escpos().writeLF(Columns.line(W, '-'));

        // Transparencia / IVA
        BigDecimal ivaContenido = t.getTax();
        p.escpos().writeLF(left, "RÉGIMEN DE TRANSPARENCIA FISCAL AL CONSUMIDOR");
        p.escpos().writeLF(left, Columns.lr("IVA Contenido", money(cfg, ivaContenido), W));
        for (var e : t.getTaxBreakdown().entrySet()) {
            p.escpos().writeLF(left, Columns.lr(e.getKey(), money(cfg, e.getValue()), W));
        }
        if (cfg.footer.legalNote != null && !cfg.footer.legalNote.isEmpty()) {
            p.escpos().writeLF(left, cfg.footer.legalNote);
        }
        p.escpos().writeLF(Columns.line(W, '-'));

        // Pagos detallados
        BigDecimal sumPagos = BigDecimal.ZERO;
        for (Payment pm : r.getPayments()) {
            p.escpos().writeLF(left, "Pago " + pm.getMethod().name());
            p.escpos().writeLF(right, money(cfg, pm.getAmount()));
            sumPagos = sumPagos.add(pm.getAmount());
        }

        // Línea final de control
        p.escpos().writeLF(Columns.line(W, '-'));
        p.escpos().writeLF(left, "Suma de sus pagos");
        p.escpos().writeLF(right, money(cfg, sumPagos));
        p.escpos().writeLF(Columns.line(W, '-'));


        // QR (CAE/link configurable)
        if (cfg.qr.enabled && cfg.qr.data != null && !cfg.qr.data.isEmpty()) {
            QRCode qr = new QRCode();
            qr.setJustification(EscPosConst.Justification.Center);
            qr.setSize(cfg.qr.size);

            p.escpos().write(qr, cfg.qr.data);

        }

        // Feed + Cut
        p.escpos().feed(cfg.paper.feedLinesBeforeCut);
        if ("TOTAL".equalsIgnoreCase(cfg.paper.cutMode)) {
            p.escpos().cut(EscPos.CutMode.FULL);
        } else {
            p.escpos().cut(EscPos.CutMode.PART);
        }
    }

    private String money(AppConfig cfg, BigDecimal v) {
        return cfg.currency + " " + v.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private BufferedImage scaleImage(BufferedImage img, int maxWidth) {
        int w = img.getWidth();
        int h = img.getHeight();

        if (w <= maxWidth) return img; // ya entra en el ancho

        int newW = maxWidth;
        int newH = (h * maxWidth) / w; // mantiene proporción

        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

}

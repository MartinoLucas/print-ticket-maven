package org.example.template;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.QRCode;
import com.github.anastaciocintra.escpos.image.*;

import org.example.config.AppConfig;
import org.example.domain.Item;
import org.example.domain.Payment;
import org.example.domain.ReceiptRequest;
import org.example.domain.ReceiptTotals;
import org.example.printer.EscPosCoffeePrinter;
import org.example.util.Columns;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class AfipBTemplate implements ReceiptTemplate {

    @Override
    public void print(EscPosCoffeePrinter p, AppConfig cfg, ReceiptRequest r, ReceiptTotals t) throws Exception {
        final int W = cfg.paper.widthChars;

        Style left = new Style().setJustification(EscPosConst.Justification.Left_Default);
        Style right = new Style().setJustification(EscPosConst.Justification.Right);
        Style center = new Style().setJustification(EscPosConst.Justification.Center);
        Style boldCenter = new Style().setBold(true).setJustification(EscPosConst.Justification.Center);
        Style boldRight = new Style().setBold(true).setJustification(EscPosConst.Justification.Right);

        // ===== LOGO =====
        if (cfg.logo != null && cfg.logo.path != null && !cfg.logo.path.isEmpty()) {
            try (InputStream is = getClass().getResourceAsStream(cfg.logo.path)) {
                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    int maxWidth = (cfg.logo.maxWidth > 0) ? cfg.logo.maxWidth : 200;
                    BufferedImage scaled = scaleImage(img, maxWidth);

                    CoffeeImageImpl coffeeImage = new CoffeeImageImpl(scaled);
                    BitonalThreshold bitonal = new BitonalThreshold();
                    EscPosImage escposImage = new EscPosImage(coffeeImage, bitonal);

                    RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
                    imageWrapper.setJustification(EscPosConst.Justification.Center);

                    p.escpos().write(imageWrapper, escposImage);
                    p.escpos().writeLF(new Style(), " ");
                    p.escpos().setStyle(new Style());
                }
            } catch (Exception ex) {
                System.err.println("No se pudo cargar logo: " + ex.getMessage());
            }
        }

        // ===== ENCABEZADO AFIP =====
        p.escpos().writeLF(Columns.line(W, '='));
        // Emisor
        p.escpos().writeLF(left, cfg.emitter.businessName);
        p.escpos().writeLF(left, cfg.emitter.address);
        p.escpos().writeLF(left, cfg.emitter.ivaCondition);

        // Datos fiscales
        p.escpos().writeLF(right, "CUIT: " + cfg.emitter.cuit);
        p.escpos().writeLF(right, "Ing.Brutos: " + cfg.emitter.iibb);
        p.escpos().writeLF(right, "Inicio Act.: " + cfg.emitter.activityStart);

        // Tipo de comprobante
        p.escpos().writeLF(boldCenter, "********** " + cfg.invoiceLabel + " **********");
        p.escpos().writeLF(center, "Código " + cfg.invoiceCode);

        // Numeración + fecha
        p.escpos().writeLF(center, "P.V. " + cfg.pvNumber + " - N° " + r.getInvoiceNumber());
        p.escpos().writeLF(center, "Fecha: " + r.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        p.escpos().writeLF(Columns.line(W, '='));

        // ===== CLIENTE =====
        if (r.getCustomerName() != null && !r.getCustomerName().isEmpty()) {
            p.escpos().writeLF(left, "Cliente: " + r.getCustomerName());
        }
        if (r.getCustomerDoc() != null && !r.getCustomerDoc().isEmpty()) {
            p.escpos().writeLF(left, "Doc: " + r.getCustomerDoc());
        } else {
            p.escpos().writeLF(left, "A CONSUMIDOR FINAL");
        }
        if (r.getCustomerAddress() != null && !r.getCustomerAddress().isEmpty()) {
            p.escpos().writeLF(left, "Dir: " + r.getCustomerAddress());
        }
        p.escpos().writeLF(Columns.line(W, '-'));

        // ===== ÍTEMS =====
        for (Item it : r.getItems()) {
            String l1 = it.getQuantity() + " x " + money(cfg, it.getUnitPrice()) + "  " + it.getDescription();
            p.escpos().writeLF(left, l1);
            String l2 = Columns.lr("", money(cfg, it.lineTotalWithIva()), W);
            p.escpos().writeLF(right, l2);
            if (it.getSku() != null && !it.getSku().isEmpty())
                p.escpos().writeLF(left, "SKU: " + it.getSku());
        }
        p.escpos().writeLF(Columns.line(W, '-'));

        // ===== TOTALES =====
        p.escpos().writeLF(boldRight, Columns.lr("SUBTOTAL", money(cfg, t.getNet()), W));
        if (t.getDiscountAmount() != null && t.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            String label = (t.getDiscountLabel() != null && !t.getDiscountLabel().isEmpty())
                    ? "DESCUENTO " + t.getDiscountLabel()
                    : "DESCUENTO";
            p.escpos().writeLF(boldRight, Columns.lr(label, "-" + money(cfg, t.getDiscountAmount()), W));
        }
        p.escpos().writeLF(boldRight, Columns.lr("TOTAL FINAL", money(cfg, t.getFinalTotal()), W));
        p.escpos().writeLF(Columns.line(W, '-'));

        // ===== PAGOS =====
        BigDecimal sumPagos = BigDecimal.ZERO;
        for (Payment pm : r.getPayments()) {
            p.escpos().writeLF(left, "Pago " + pm.getMethod().name());
            p.escpos().writeLF(right, money(cfg, pm.getAmount()));
            sumPagos = sumPagos.add(pm.getAmount());
        }
        p.escpos().writeLF(Columns.line(W, '-'));
        p.escpos().writeLF(boldRight, Columns.lr("TOTAL PAGADO", money(cfg, sumPagos), W));
        p.escpos().writeLF(Columns.line(W, '-'));

        // ===== IVA =====
        p.escpos().writeLF(left, "Régimen de Transparencia Fiscal al Consumidor (Ley 27.743)");
        p.escpos().writeLF(left, Columns.lr("IVA Contenido", money(cfg, t.getTax()), W));
        for (var e : t.getTaxBreakdown().entrySet()) {
            p.escpos().writeLF(left, Columns.lr(e.getKey(), money(cfg, e.getValue()), W));
        }

        // ===== CAE y Vto =====
        p.escpos().writeLF(Columns.line(W, '-'));
        p.escpos().writeLF(left, "C.A.E. N°: " + cfg.authorization.code);
        p.escpos().writeLF(left, "Fecha Vto.: " + cfg.authorization.expiration);

        // ===== QR =====
        if (cfg.qr.enabled && cfg.qr.data != null && !cfg.qr.data.isEmpty()) {
            QRCode qr = new QRCode();
            qr.setJustification(EscPosConst.Justification.Center);
            qr.setSize(cfg.qr.size);
            p.escpos().write(qr, cfg.qr.data);
        }

        // Cut
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
        if (w <= maxWidth) return img;
        int newW = maxWidth;
        int newH = (h * maxWidth) / w;

        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}

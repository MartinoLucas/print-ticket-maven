package org.example.config;

public class AppConfig {
    public String storeName;
    public String phone;
    public String pvNumber;
    public String invoiceLabel;
    public String currency;

    // 🆕 Datos del emisor (AFIP)
    public Emitter emitter = new Emitter();
    public static class Emitter {
        public String name;           // Razón social
        public String businessName;   // Nombre de fantasía
        public String address;        // Domicilio comercial
        public String cuit;           // CUIT emisor
        public String iibb;           // N° ingresos brutos o leyenda "Exento"
        public String ivaCondition;   // Ej: "IVA RESPONSABLE INSCRIPTO"
        public String activityStart;  // Fecha de inicio de actividades
    }

    // 🆕 Datos del comprobante
    public String invoiceLetter;  // A, B, C o E
    public String invoiceCode;    // Código identificatorio
    public Authorization authorization = new Authorization();
    public static class Authorization {
        public String code;        // N° CAE / CAI
        public String expiration;  // Fecha de vencimiento del CAE/CAI
    }

    // Config existentes
    public Paper paper = new Paper();
    public PrinterCfg printer = new PrinterCfg();
    public Footer footer = new Footer();
    public Qr qr = new Qr();
    public LogoConfig logo;

    public static class LogoConfig {
        public String path;
        public int maxWidth;
    }

    public static class Paper {
        public int widthChars = 64;
        public int feedLinesBeforeCut = 5;
        public String cutMode = "PART";
    }

    public static class PrinterCfg {
        public String mode = "DEFAULT";
        public String name = "";
    }

    public static class Footer {
        public String consumerDefense = "";
        public String legalNote = "";
    }

    public static class Qr {
        public boolean enabled = true;
        public String data = "";
        public int size = 6;
    }
}

package org.example.config;

public class AppConfig {
    public String storeName;
    public String address;
    public String phone;
    public String pvNumber;
    public String invoiceLabel;
    public String currency;
    public Paper paper = new Paper();
    public PrinterCfg printer = new PrinterCfg();
    public Footer footer = new Footer();
    public Qr qr = new Qr();

    public static class LogoConfig {
        public String path;
        public int maxWidth;
    }
    public LogoConfig logo;


    public static class Paper { public int widthChars = 48; public int feedLinesBeforeCut = 5; public String cutMode = "PART"; }
    public static class PrinterCfg { public String mode = "DEFAULT"; public String name = ""; }
    public static class Footer { public String consumerDefense = ""; public String legalNote = ""; }
    public static class Qr { public boolean enabled = true; public String data = ""; public int size = 6; }
}

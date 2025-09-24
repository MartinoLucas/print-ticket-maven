package org.example.domain;

import java.math.BigDecimal;

public class Payment {
    public enum Method { CASH, CARD, MP, TRANSFER }
    private final Method method;
    private final BigDecimal amount;
    public Payment(Method method, BigDecimal amount) { this.method = method; this.amount = amount; }
    public Method getMethod() { return method; }
    public BigDecimal getAmount() { return amount; }
}

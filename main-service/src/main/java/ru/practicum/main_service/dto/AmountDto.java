package ru.practicum.main_service.dto;

import java.math.BigDecimal;

public class AmountDto {
    private BigDecimal total;
    private String currency;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

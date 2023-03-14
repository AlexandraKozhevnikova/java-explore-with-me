package ru.practicum.main_service.dto.event;


import java.math.BigDecimal;

public class EvenPaymentsReport {
    private Long eventId;

    private BigDecimal totalAmount;
    private Long paymentsCount;
    private Double averagePayment;

    public EvenPaymentsReport(Long eventId, BigDecimal totalAmount, Long paymentsCount, Double averagePayment) {
        this.eventId = eventId;
        this.totalAmount = totalAmount;
        this.paymentsCount = paymentsCount;
        this.averagePayment = averagePayment;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getPaymentsCount() {
        return paymentsCount;
    }

    public void setPaymentsCount(Long paymentsCount) {
        this.paymentsCount = paymentsCount;
    }

    public Double getAveragePayment() {
        return averagePayment;
    }

    public void setAveragePayment(Double averagePayment) {
        this.averagePayment = averagePayment;
    }
}

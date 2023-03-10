package ru.practicum.main_service.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill")
public class BillEntity {
    @Id
    @GeneratedValue
    @Column(name = "bill_id")
    private Long billId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private UserEntity participant;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private CurrencyEntity currency;
    @Enumerated
    private BillState state;
    @Column(name = "created_on")
    @CreationTimestamp
    private LocalDateTime createdOn;

}

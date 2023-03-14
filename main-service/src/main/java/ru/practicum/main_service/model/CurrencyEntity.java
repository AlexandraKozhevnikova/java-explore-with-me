package ru.practicum.main_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "currency")
public class CurrencyEntity {
    @Id
    @GeneratedValue
    @Column(name = "currency_id")
    private Long currencyId;
    @Column(nullable = false, length = 50, unique = true)
    private String title;

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyEntity that = (CurrencyEntity) o;

        return currencyId.equals(that.currencyId);
    }

    @Override
    public int hashCode() {
        return currencyId.hashCode();
    }
}

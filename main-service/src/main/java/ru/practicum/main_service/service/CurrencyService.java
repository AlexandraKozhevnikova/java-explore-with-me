package ru.practicum.main_service.service;

import org.springframework.stereotype.Service;
import ru.practicum.main_service.model.CurrencyEntity;
import ru.practicum.main_service.repository.CurrencyRepository;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public CurrencyEntity getCurrencyRub() {
        return currencyRepository.findById(1l).get();
    }

    public CurrencyEntity getCurrencyUsd() {
        return currencyRepository.findById(2l).get();
    }

    public CurrencyEntity getCurrencyByTitle(String title) {
        switch (title) {
            case "RUB":
                return getCurrencyRub();

            case "USD":
                return getCurrencyUsd();
            default:
                throw new IllegalArgumentException(title + " - is unknown currency");
        }
    }
}

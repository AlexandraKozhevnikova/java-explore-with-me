package ru.practicum.main_service.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface BillCustomRepository {

    List<Tuple> getEventPaymentsReport(Long initiatorId);
}

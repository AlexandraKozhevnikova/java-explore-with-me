package ru.practicum.main_service.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.main_service.model.RequestEntity;

import java.util.List;

public interface RequestRepository extends JpaRepository<RequestEntity, Long>,
        QuerydslPredicateExecutor<RequestEntity> {

    List<RequestEntity> findAll(Predicate predicate);

}

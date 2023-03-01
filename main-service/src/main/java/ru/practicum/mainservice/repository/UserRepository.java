package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainservice.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>, QuerydslPredicateExecutor<UserEntity>,
        UserCustomRepository {
}

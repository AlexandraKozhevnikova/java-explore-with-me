package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.AppEntity;

public interface AppRepository extends JpaRepository<AppEntity, Long> {

    AppEntity getAppEntityByName(String name);
}

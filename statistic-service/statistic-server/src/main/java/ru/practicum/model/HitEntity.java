package ru.practicum.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hit")
public class HitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false, length = 255)
    private String app;
    @Column(nullable = false, length = 255)
    private String uri;
    @Column(nullable = false, length = 255)
    private String ip;
    @Column(name = "date_time", nullable = false, length = 255
    )
    private LocalDateTime dateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HitEntity hitEntity = (HitEntity) o;
        return id.equals(hitEntity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

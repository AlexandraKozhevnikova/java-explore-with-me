package ru.practicum.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "app")
public class AppEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long appId;
    @Column(name = "title", nullable = false, unique = true)
    String name;
    @OneToMany
    @JoinColumn(name = "app_id")
    @LazyCollection(LazyCollectionOption.TRUE)
    List<HitEntity> hits;

    public AppEntity() {
    }

    public AppEntity(String name) {
        this.name = name;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HitEntity> getHits() {
        return hits;
    }

    public void setHits(List<HitEntity> hits) {
        this.hits = hits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppEntity appEntity = (AppEntity) o;
        return appId.equals(appEntity.appId);
    }

    @Override
    public int hashCode() {
        return appId.hashCode();
    }

    @Override
    public String toString() {
        return "AppEntity{" +
            "appId=" + appId +
            ", name='" + name + '\'' +
            '}';
    }
}

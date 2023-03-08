package ru.practicum.main_service.dto.event;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LocationDto {
    @NotNull
    private Double lat;
    @NotNull
    private Double lon;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}

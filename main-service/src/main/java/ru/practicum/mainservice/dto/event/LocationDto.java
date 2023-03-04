package ru.practicum.mainservice.dto.event;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LocationDto {
    @NotNull
    private BigDecimal lat;
    @NotNull
    private BigDecimal lon;

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }
}

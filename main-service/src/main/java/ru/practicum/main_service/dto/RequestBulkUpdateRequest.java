package ru.practicum.main_service.dto;

import ru.practicum.main_service.model.RequestState;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class RequestBulkUpdateRequest {
    @NotEmpty
    private List<Long> requestIds;
    @NotNull
    private RequestState status;

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
    }

    public RequestState getStatus() {
        return status;
    }

    public void setStatus(RequestState status) {
        this.status = status;
    }
}

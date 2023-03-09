package ru.practicum.main_service.dto;

import java.util.List;

public class RequestBulkUpdateResponse {
    List<RequestResponse> confirmedRequests;
    List<RequestResponse> rejectedRequests;

    public List<RequestResponse> getConfirmedRequests() {
        return confirmedRequests;
    }

    public void setConfirmedRequests(List<RequestResponse> confirmedRequests) {
        this.confirmedRequests = confirmedRequests;
    }

    public List<RequestResponse> getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(List<RequestResponse> rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }
}

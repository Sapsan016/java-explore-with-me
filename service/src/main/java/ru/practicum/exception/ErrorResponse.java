package ru.practicum.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {


    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;

    public ErrorResponse(String message, String status, String reason, String timestamp) {
        this.message = message;
        this.status = status;
        this.reason = reason;
        this.timestamp = timestamp;
    }

}

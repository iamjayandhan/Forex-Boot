package gomobi.io.forex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class SuccessResponse<T> {
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Only include 'data' if it's not null
    private T data;

    // Constructor for both success and failure responses
    public SuccessResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    //success may or may not have extra data!
    public SuccessResponse(int status, String message) {
        this(status, message, null);
    }

    // Getters and setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

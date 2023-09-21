package com.mtg.error;

import java.util.Date;

public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String error;
    private String path;

    public ErrorMessage(Date timestamp, int statusCode, String error, String path) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.error = error;
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}

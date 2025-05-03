package com.org.takehome.exceptions;

public class DownstreamServiceException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public DownstreamServiceException(int statusCode, String responseBody) {
        super("Downstream service error with status " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}

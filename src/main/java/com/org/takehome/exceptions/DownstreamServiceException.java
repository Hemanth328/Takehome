package com.org.takehome.exceptions;

import lombok.Getter;

@Getter
public class DownstreamServiceException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public DownstreamServiceException(int statusCode, String responseBody) {
        super("Downstream service error with status " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}

package com.org.takehome.exceptions;

import com.org.takehome.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleDownstreamResponse(DownstreamServiceException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                request.getDescription(false),
                exception.getStatusCode(),
                exception.getResponseBody()
        );

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(errorResponseDto);
    }
}

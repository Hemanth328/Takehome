package com.org.takehome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String path;
    private int statusCode;
    private String exceptionMessage;
}

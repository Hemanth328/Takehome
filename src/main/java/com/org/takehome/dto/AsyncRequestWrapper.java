package com.org.takehome.dto;
import com.org.takehome.enums.ApiMethod;
import lombok.Data;

@Data
public class AsyncRequestWrapper {
    private ApiMethod apiMethod;
    private RequestDto requestDTO;
    private int timeout;
}


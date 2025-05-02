package com.org.takehome.dto;

import com.org.takehome.enums.ApiMethod;
import lombok.Data;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;
@Data
public class RequestDto {
    private ApiMethod apiMethod;
    private String url;
    private Map<String, String> queryParams;
    private Map<String, String> headerVariables;
    private String bodyType;
    private String requestBody;
    private Map<String, String> pathMap;
    private Map<String, String> formParam;
    private Map<String, String> urlEncodedParam;
    private List<NameValuePair> params;
}


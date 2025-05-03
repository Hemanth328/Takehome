package com.org.takehome.service;

import com.org.takehome.dto.RequestDto;
import com.org.takehome.enums.ApiMethod;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

public interface ApiFactory {
    public abstract CompletableFuture<String> executeTarget(
            ApiMethod apiMethod,
            RequestDto requestDTO,
            SSLContext sslContext,
            int timeout
    ) throws NoSuchAlgorithmException, KeyManagementException;
}


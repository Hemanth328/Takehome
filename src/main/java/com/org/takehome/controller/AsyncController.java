package com.org.takehome.controller;

import com.org.takehome.dto.AsyncRequestWrapper;
import com.org.takehome.dto.RequestDto;
import com.org.takehome.enums.ApiMethod;
import com.org.takehome.service.ApiFactory;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/invoke")
public class AsyncController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncController.class);

    private final ApiFactory restFactoryAsync;

    public AsyncController(ApiFactory restFactoryAsync) {
        this.restFactoryAsync = restFactoryAsync;
    }

    @Retry(name = "async-retry", fallbackMethod = "handlePostRequestFallback")
    @PostMapping("/post")
    public CompletableFuture<ResponseEntity<String>> handlePostRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {

        logger.info("handlePostRequest :  Received request for async post");

        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDto();
        int timeout = requestWrapper.getTimeout();

        // SSL factory is optional, setting to null for now. You can customize this later.
        SSLConnectionSocketFactory sslFactory = null;

        return restFactoryAsync.executeTarget(method, requestDto, sslFactory, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    public CompletableFuture<ResponseEntity<String>> handlePostRequestFallback(@RequestBody AsyncRequestWrapper requestWrapper, Throwable throwable) {
        logger.info("handlePostRequestFallback : Fallback method executed");
        return CompletableFuture.completedFuture(ResponseEntity.ok().body("Fallback PostRequest Response"));
    }

    @GetMapping("/map")
    public CompletableFuture<ResponseEntity<String>> handleRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDto();
        int timeout = requestWrapper.getTimeout();

        // SSL factory is optional, setting to null for now. You can customize this later.
        SSLConnectionSocketFactory sslFactory = null;

        return restFactoryAsync.executeTarget(method, requestDto, sslFactory, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    @PatchMapping("/patch")
    public CompletableFuture<ResponseEntity<String>> handlePatchRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDto();
        int timeout = requestWrapper.getTimeout();

        // SSL factory is optional, setting to null for now. You can customize this later.
        SSLConnectionSocketFactory sslFactory = null;

        return restFactoryAsync.executeTarget(method, requestDto, sslFactory, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    @PutMapping("/put")
    public CompletableFuture<ResponseEntity<String>> handlePutRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDto();
        int timeout = requestWrapper.getTimeout();

        // SSL factory is optional, setting to null for now. You can customize this later.
        SSLConnectionSocketFactory sslFactory = null;

        return restFactoryAsync.executeTarget(method, requestDto, sslFactory, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<String>> handleDeleteRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDto();
        int timeout = requestWrapper.getTimeout();

        // SSL factory is optional, setting to null for now. You can customize this later.
        SSLConnectionSocketFactory sslFactory = null;

        return restFactoryAsync.executeTarget(method, requestDto, sslFactory, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }
}



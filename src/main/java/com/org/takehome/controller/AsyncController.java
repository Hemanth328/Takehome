package com.org.takehome.controller;

import com.org.takehome.dto.AsyncRequestWrapper;
import com.org.takehome.dto.RequestDto;
import com.org.takehome.enums.ApiMethod;
import com.org.takehome.service.ApiFactory;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/invoke")
public class AsyncController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncController.class);

    private final ApiFactory restFactoryAsync;

    public AsyncController(ApiFactory restFactoryAsync) {
        this.restFactoryAsync = restFactoryAsync;
    }

    @Retry(name = "asyncRetry", fallbackMethod = "handlePostRequestFallback")
    @PostMapping("/post")
    public CompletableFuture<ResponseEntity<String>> handlePostRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        logger.info("handlePostRequest :  Received request for async post");
        logger.debug("handlePostRequest :  Received request for async post");


        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDTO();
        int timeout = requestWrapper.getTimeout();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, new SecureRandom());


        return restFactoryAsync.executeTarget(method, requestDto, sslContext, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    public CompletableFuture<ResponseEntity<String>> handlePostRequestFallback(@RequestBody AsyncRequestWrapper requestWrapper, Throwable throwable) {
        logger.info("handlePostRequestFallback : Fallback method executed");
        logger.debug("handlePostRequestFallback : Fallback method executed");
        return CompletableFuture.completedFuture(ResponseEntity.ok().body("Fallback PostRequest Response"));
    }

    @Retry(name = "async-retry", fallbackMethod = "handleGetRequestFallback")
    @GetMapping("/map")
    public CompletableFuture<ResponseEntity<String>> handleGetRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.debug("handleGetRequest :  Received request for async get");
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDTO();
        int timeout = requestWrapper.getTimeout();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, new SecureRandom());


        return restFactoryAsync.executeTarget(method, requestDto, sslContext, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    public CompletableFuture<ResponseEntity<String>> handleGetRequestFallback(@RequestBody AsyncRequestWrapper requestWrapper, Throwable throwable) throws NoSuchAlgorithmException, KeyManagementException {
        logger.debug("handlePostRequestFallback : Fallback method executed");
        return CompletableFuture.completedFuture(ResponseEntity.ok().body("Fallback GetRequest Response"));
    }

    @PatchMapping("/patch")
    public CompletableFuture<ResponseEntity<String>> handlePatchRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDTO();
        int timeout = requestWrapper.getTimeout();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, new SecureRandom());

        return restFactoryAsync.executeTarget(method, requestDto, sslContext, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    @PutMapping("/put")
    public CompletableFuture<ResponseEntity<String>> handlePutRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDTO();
        int timeout = requestWrapper.getTimeout();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, new SecureRandom());

        return restFactoryAsync.executeTarget(method, requestDto, sslContext, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<String>> handleDeleteRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        ApiMethod method = requestWrapper.getApiMethod();
        RequestDto requestDto = requestWrapper.getRequestDTO();
        int timeout = requestWrapper.getTimeout();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, new SecureRandom());

        return restFactoryAsync.executeTarget(method, requestDto, sslContext, timeout)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Error: " + ex.getMessage()));
    }
}



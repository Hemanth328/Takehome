package com.org.takehome.controller;

import com.org.takehome.dto.AsyncRequestWrapper;
import com.org.takehome.dto.RequestDto;
import com.org.takehome.enums.ApiMethod;
import com.org.takehome.service.ApiFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
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

    @PostMapping("/post")
    public CompletableFuture<ResponseEntity<String>> handlePostRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.info("handlePostRequest :  Received request for async post");
        return handleRequest(requestWrapper);
    }


    @GetMapping("/get")
    public CompletableFuture<ResponseEntity<String>> handleGetRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.debug("handleGetRequest :  Received request for async get");
        return handleRequest(requestWrapper);
    }


    @PatchMapping("/patch")
    public CompletableFuture<ResponseEntity<String>> handlePatchRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.debug("handlePatchRequest :  Received request for async patch");
        return handleRequest(requestWrapper);
    }

    @PutMapping("/put")
    public CompletableFuture<ResponseEntity<String>> handlePutRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.debug("handlePutRequest :  Received request for async put");
        return handleRequest(requestWrapper);
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<String>> handleDeleteRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.debug("handleDeleteRequest :  Received request for async delete");
        return handleRequest(requestWrapper);
    }

    @RequestMapping(value="/options", method={RequestMethod.OPTIONS,RequestMethod.GET})
    public CompletableFuture<ResponseEntity<String>> handleOptionsRequest(@RequestBody AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.debug("handleOptionsRequest :  Received request for async options");
        return handleRequest(requestWrapper);
    }

    private CompletableFuture<ResponseEntity<String>> handleRequest(AsyncRequestWrapper requestWrapper) throws NoSuchAlgorithmException, KeyManagementException {
        logger.info("handleRequest :  Received request for async");

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



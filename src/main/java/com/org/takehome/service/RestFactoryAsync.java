package com.org.takehome.service;

import com.org.takehome.dto.RequestDto;
import com.org.takehome.enums.ApiMethod;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@Service
public class RestFactoryAsync implements ApiFactory{

    private static final Logger logger = LoggerFactory.getLogger(RestFactoryAsync.class);

    @Override
    @Async
    @Retryable(
            value = { IOException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public CompletableFuture<String> executeTarget(ApiMethod apiMethod, RequestDto dto,
                                                   SSLContext sslContext, int timeout) throws NoSuchAlgorithmException, KeyManagementException {

        return switch (apiMethod) {
            case GET -> invokeAsync(new HttpGet(dto.getUrl()), dto, sslContext, timeout);
            case DELETE -> {
                HttpDelete delete = new HttpDelete(dto.getUrl());
                yield invokeAsync(delete, dto, sslContext, timeout);
            }
            case OPTIONS -> {
                HttpOptions options = new HttpOptions(dto.getUrl());
                yield invokeAsync(options, dto, sslContext, timeout);
            }
            case PATCH -> {
                HttpPatch patch = new HttpPatch(dto.getUrl());
                if (dto.getRequestBody() != null) {
                    patch.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield invokeAsync(patch, dto, sslContext, timeout);
            }
            case POST -> {
                HttpPost post = new HttpPost(dto.getUrl());
                if (dto.getRequestBody() != null) {
                    post.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield invokeAsync(post, dto, sslContext, timeout);
            }
            case PUT -> {
                HttpPut put = new HttpPut(dto.getUrl());
                if (dto.getRequestBody() != null) {
                    put.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield invokeAsync(put, dto, sslContext, timeout);
            }
        };
    }

    private CompletableFuture<String> invokeAsync(HttpRequestBase request,
                                                  RequestDto dto,
                                                  SSLContext sslContext,
                                                  int timeout) throws NoSuchAlgorithmException, KeyManagementException {

        CompletableFuture<String> future = new CompletableFuture<>();

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();

        request.setConfig(config);

        // Add headers
        if (dto.getHeaderVariables() != null) {
            dto.getHeaderVariables().forEach(request::addHeader);
        }

        // Build async client
        HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom()
                .setDefaultRequestConfig(config);

        if (sslContext != null) {
            clientBuilder.setSSLStrategy(new SSLIOSessionStrategy(
                    sslContext,
                    new String[]{"TLSv1.2", "TLSv1.3"},
                    null,
                    NoopHostnameVerifier.INSTANCE
            ));
        }

        clientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);

        CloseableHttpAsyncClient client = clientBuilder.build();
        client.start();

        client.execute(request, new FutureCallback<>() {
            @Override
            public void completed(HttpResponse result) {
                try {
                    int statusCode = result.getStatusLine().getStatusCode();
                    String responseString = EntityUtils.toString(result.getEntity(), StandardCharsets.UTF_8);

                    if(statusCode >= 200 && statusCode <300 || statusCode == 403) {
                        logger.info("Response from target: {}", responseString);
                        future.complete(responseString);
                    } else {
                        logger.error("Error from target: {}", responseString);
                        future.completeExceptionally(new IOException(responseString));
                    }

                } catch (IOException e) {
                    future.completeExceptionally(e);
                } finally {
                    closeClient(client);
                }
            }

            @Override
            public void failed(Exception ex) {
                future.completeExceptionally(ex);
                closeClient(client);
            }

            @Override
            public void cancelled() {
                future.cancel(true);
                closeClient(client);
            }
        });

        return future;
    }

    private void closeClient(CloseableHttpAsyncClient client) {
        try {
            client.close();
        } catch (IOException e) {
            logger.error("Error closing async client", e);
        }
    }

    @Recover
    public CompletableFuture<String> recover(IOException e, ApiMethod apiMethod, RequestDto dto,
                                             SSLContext sslContext, int timeout) {
        logger.error("Failed after retries: {}", e.getMessage());
        return CompletableFuture.completedFuture("Fallback response due to error: " + e.getMessage());
    }
}

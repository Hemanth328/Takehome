package com.org.takehome.service;

import com.org.takehome.dto.RequestDto;
import com.org.takehome.enums.ApiMethod;
import com.org.takehome.ssl.SSLConnectionSocketFactoryAdapter;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.util.EntityUtils;
import org.slf4j.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@Service
public class RestFactoryAsync implements ApiFactory{

    private static final Logger logger = LoggerFactory.getLogger(RestFactoryAsync.class);

    @Override
    public CompletableFuture<String> executeTarget(ApiMethod apiMethod, RequestDto dto,
                                                   SSLConnectionSocketFactory sslConnectionSocketFactory, int timeout) throws NoSuchAlgorithmException, KeyManagementException {

        return switch (apiMethod) {
            case GET -> invokeAsync(new HttpGet(dto.getUrl()), dto, sslConnectionSocketFactory, timeout);
            case DELETE -> {
                HttpDelete delete = new HttpDelete(dto.getUrl());
                yield invokeAsync(delete, dto, sslConnectionSocketFactory, timeout);
            }
            case OPTIONS -> {
                HttpOptions options = new HttpOptions(dto.getUrl());
                yield invokeAsync(options, dto, sslConnectionSocketFactory, timeout);
            }
            case PATCH -> {
                HttpPatch patch = new HttpPatch(dto.getUrl());
                if (dto.getRequestBody() != null) {
                    patch.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield invokeAsync(patch, dto, sslConnectionSocketFactory, timeout);
            }
            case POST -> {
                HttpPost post = new HttpPost(dto.getUrl());
                if (dto.getRequestBody() != null) {
                    post.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield invokeAsync(post, dto, sslConnectionSocketFactory, timeout);
            }
            case PUT -> {
                HttpPut put = new HttpPut(dto.getUrl());
                if (dto.getRequestBody() != null) {
                    put.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield invokeAsync(put, dto, sslConnectionSocketFactory, timeout);
            }
        };
    }

    private CompletableFuture<String> invokeAsync(HttpRequestBase request,
                                                  RequestDto dto,
                                                  SSLConnectionSocketFactory sslConnectionSocketFactory,
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

        if (sslConnectionSocketFactory != null) {
            clientBuilder.setSSLStrategy(new SSLConnectionSocketFactoryAdapter(sslConnectionSocketFactory));
        }

        CloseableHttpAsyncClient client = clientBuilder.build();
        client.start();

        client.execute(request, new FutureCallback<>() {
            @Override
            public void completed(HttpResponse result) {
                try {
                    String responseString = EntityUtils.toString(result.getEntity(), StandardCharsets.UTF_8);
                    future.complete(responseString);
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
}

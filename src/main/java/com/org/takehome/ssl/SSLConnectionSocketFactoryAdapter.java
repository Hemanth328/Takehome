package com.org.takehome.ssl;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class SSLConnectionSocketFactoryAdapter extends SSLIOSessionStrategy {

    public SSLConnectionSocketFactoryAdapter(SSLConnectionSocketFactory factory) throws NoSuchAlgorithmException, KeyManagementException {
        super(new SSLContextBuilder().build(), (hostname, session) -> true); // Accept all hosts for simplicity
    }
}



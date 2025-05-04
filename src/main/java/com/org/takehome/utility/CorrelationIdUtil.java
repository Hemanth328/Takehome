package com.org.takehome.utility;


import java.util.UUID;

public class CorrelationIdUtil {

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}


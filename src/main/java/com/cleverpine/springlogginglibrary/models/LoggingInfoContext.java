package com.cleverpine.springlogginglibrary.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoggingInfoContext {
    private static LoggingInfoContext instance;
    private String serviceId;
    private final ThreadLocal<String> traceId = new ThreadLocal<>();

    private Map<String, String> properties = new HashMap<>();

    private LoggingInfoContext() {}

    public static LoggingInfoContext getInstance() {
        if (instance == null) {
            instance = new LoggingInfoContext();
        }
        return instance;
    }

    public String generateTraceId(){
        return UUID.randomUUID().toString();
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getTraceId() {
        return traceId.get();
    }

    public void setTraceId(String traceId) {
        this.traceId.set(traceId);
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void putProperty(String key, String value) {
        properties.put(key, value);
    }
}

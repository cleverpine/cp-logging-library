package com.cleverpine.springlogginglibrary.util;

public class Constants {
    public static final String SERVICE_ID = "serviceId";
    public static final String TRACE_ID = "traceId";

    public static final String DEFAULT_LOGGING_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS}[%X{traceId}][%X{serviceId}][%C][%p] - %msg%n%throwable";

    public static final String STDOUT_APPENDER_NAME = "Stdout";
    public static final String LOGSTASH_APPENDER_NAME = "Logstash";
    public static final int RECONNECT_DELAY = 5000;
}

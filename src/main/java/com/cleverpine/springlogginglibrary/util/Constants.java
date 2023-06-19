package com.cleverpine.springlogginglibrary.util;

public class Constants {
    public static final String SERVICE_ID = "serviceId";
    public static final String TRACE_ID = "traceId";

    public static final String DEFAULT_LOGGING_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS}[%X{traceId}][%X{serviceId}][%C][%p] - %msg%n%throwable";

    public static final String STDOUT_APPENDER_NAME = "Stdout";
    public static final String LOGSTASH_APPENDER_NAME = "Logstash";
    public static final int RECONNECT_DELAY = 5000;

    public static final String JSON_TEMPLATE_LOGSTASH = """
            {
              "@timestamp": "${json:timeMillis}",
              "@version": "1",
              "message": "${json:message}",
              "logger_name": "${json:loggerName}",
              "thread_name": "${json:threadName}",
              "level": "${json:level}",
              "level_value": "${json:levelValue}",
              "stack_trace": "${json:thrown:stackTrace:text}",
              "#if($${json:contextMap:isEmpty})": {
                "#drop": {}
              },
              "#else": {
                "${json:contextMap}": {}
              }
            }
            """;
}

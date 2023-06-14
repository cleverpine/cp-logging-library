package com.cleverpine.springlogginglibrary.configuration;

import com.cleverpine.springlogginglibrary.models.LoggingInfoContext;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.SocketAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.JsonLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


import static com.cleverpine.springlogginglibrary.util.Constants.DEFAULT_LOGGING_PATTERN;
import static com.cleverpine.springlogginglibrary.util.Constants.LOGSTASH_APPENDER_NAME;
import static com.cleverpine.springlogginglibrary.util.Constants.RECONNECT_DELAY;

@Component
@ConditionalOnProperty(name = "logging.logstash.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "logging.logstash")
public class LogstashLoggingConfig {
    Logger log = LogManager.getLogger(LogstashLoggingConfig.class);

    @Value("${logging.logstash.host}")
    private String logstashHost;

    @Value("${logging.logstash.port}")
    private String logstashPort;

    private Map<String, String> properties;

    @PostConstruct
    public void initLogstashLogging(){
        log.info("Initializing Logstash Logging on host: " + logstashHost + " and port: " + logstashPort + "...");
        //Get Configuration Builder from current context
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        var configuration = loggerContext.getConfiguration();

        //Setting the Thread context properties for all threads inheriting from the main thread
        System.setProperty("log4j2.isThreadContextMapInheritable", "true");
        properties.forEach(ThreadContext::put);

        //Setting the properties into the LoggingInfoContext
        var loggingInfoContext = LoggingInfoContext.getInstance();
        properties.forEach(loggingInfoContext::putProperty);

        //Create the new appender
        var layout = JsonLayout.newBuilder()
                .setLocationInfo(true)
                .setProperties(true)
                .setPropertiesAsList(false)
                .setIncludeStacktrace(true)
                .setEventEol(true)
                .setCompact(true)
                .build();

        var socketAppender = SocketAppender.newBuilder()
                .setHost(logstashHost)
                .setPort(Integer.parseInt(logstashPort))
                .setReconnectDelayMillis(RECONNECT_DELAY)
                .setName(LOGSTASH_APPENDER_NAME)
                .setLayout(layout)
                .setImmediateFlush(true)
                .build();
        socketAppender.start();

        //Set the appender into all active loggers
        configuration.getLoggers().forEach((s, loggerConfig) -> {
            loggerConfig.addAppender(socketAppender, null, null);
        });
        configuration.getRootLogger().addAppender(socketAppender, null, null);

        loggerContext.updateLoggers();
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
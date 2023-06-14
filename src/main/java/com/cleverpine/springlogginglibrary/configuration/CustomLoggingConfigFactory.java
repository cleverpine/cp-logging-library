package com.cleverpine.springlogginglibrary.configuration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;


import static com.cleverpine.springlogginglibrary.util.Constants.DEFAULT_LOGGING_PATTERN;
import static com.cleverpine.springlogginglibrary.util.Constants.STDOUT_APPENDER_NAME;

@Plugin(
        name = "CustomLoggingConfigFactory",
        category = ConfigurationFactory.CATEGORY
)
@Order(Integer.MAX_VALUE)
public class CustomLoggingConfigFactory extends ConfigurationFactory {

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }

    static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(Level.ERROR);
        //Configure Appenders
        configurePredefinedAppenders(builder);
        //CreateLogger and RootLogger
        configurePredefinedLoggers(builder);

        return builder.build();
    }

    private static void configurePredefinedAppenders(ConfigurationBuilder<BuiltConfiguration> builder){
        var defaultLayout = builder.newLayout("PatternLayout")
                .addAttribute("pattern", DEFAULT_LOGGING_PATTERN);

        var consoleAppender = builder.newAppender(STDOUT_APPENDER_NAME, "Console");
        consoleAppender.add(defaultLayout);
        builder.add(consoleAppender);
    }

    private static void configurePredefinedLoggers(ConfigurationBuilder<BuiltConfiguration> builder){
        LoggerComponentBuilder logger = builder.newLogger("com", Level.INFO);
        logger.add(builder.newAppenderRef(STDOUT_APPENDER_NAME));
        logger.addAttribute("additivity", false);
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ERROR);
        rootLogger.add(builder.newAppenderRef(STDOUT_APPENDER_NAME));
        rootLogger.addAttribute("additivity", false);

        builder.add(logger);
        builder.add(rootLogger);
    }
}

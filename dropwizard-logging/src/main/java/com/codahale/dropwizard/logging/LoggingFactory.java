package com.codahale.dropwizard.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.jmx.JMXConfigurator;
import com.yammer.metrics.logback.InstrumentedAppender;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;

// TODO: 11/7/11 <coda> -- document LoggingFactory
// TODO: 11/7/11 <coda> -- test LoggingFactory

public class LoggingFactory {
    private final LoggingConfiguration config;
    private final String name;

    public LoggingFactory(LoggingConfiguration config, String name) {
        this.config = config;
        this.name = name;
    }

    public void configure() {
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        configureLevels(root);

        for (LoggingOutput output : config.getOutputs()) {
            root.addAppender(output.build(root.getLoggerContext(), name, null));
        }

        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            final ObjectName objectName = new ObjectName("com.yammer:type=Logging");
            if (!server.isRegistered(objectName)) {
                server.registerMBean(new JMXConfigurator(root.getLoggerContext(),
                                                         server,
                                                         objectName),
                                     objectName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        configureInstrumentation(root);
    }

    private void configureInstrumentation(Logger root) {
        final InstrumentedAppender appender = new InstrumentedAppender();
        appender.setContext(root.getLoggerContext());
        appender.start();
        root.addAppender(appender);
    }

    private void configureLevels(Logger root) {
        root.setLevel(config.getLevel());

        for (Map.Entry<String, Level> entry : config.getLoggers().entrySet()) {
            ((Logger) LoggerFactory.getLogger(entry.getKey())).setLevel(entry.getValue());
        }
    }
}

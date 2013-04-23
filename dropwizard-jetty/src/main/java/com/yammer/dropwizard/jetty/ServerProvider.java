package com.yammer.dropwizard.jetty;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;
import com.yammer.dropwizard.logging.LoggingOutput;
import com.yammer.dropwizard.util.Duration;
import com.yammer.dropwizard.util.Size;
import com.yammer.metrics.core.Clock;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.BlockingQueue;

public class ServerProvider implements Provider<Server> {
    private static class RequestLogLayout extends LayoutBase<ILoggingEvent> {
        @Override
        public String doLayout(ILoggingEvent event) {
            return event.getFormattedMessage() + CoreConstants.LINE_SEPARATOR;
        }
    }

    private final ServerConfiguration config;
    private final ServletContextHandler applicationHandler;
    private final ServletContextHandler adminHandler;
    private final String name;

    @Inject
    public ServerProvider(ServerConfiguration config,
                          @Named("application") ServletContextHandler applicationHandler,
                          @Named("admin") ServletContextHandler adminHandler,
                          @Named("application") String name) {
        this.config = config;
        this.applicationHandler = applicationHandler;
        this.adminHandler = adminHandler;
        this.name = name;
    }

    @Override
    public Server get() {
        final ThreadPool threadPool = createThreadPool();

        final Server server = new Server(threadPool);

        final Connector applicationConnector = createApplicationConnector(server);
        server.addConnector(applicationConnector);

        final Connector adminConnector;
        // if we're dynamically allocating ports, no worries if they are the same (i.e. 0)
        if (config.getAdminPort() == 0 || (config.getAdminPort() != config.getPort())) {
            adminConnector = createAdminConnector(server);
            server.addConnector(adminConnector);
        } else {
            adminConnector = applicationConnector;
        }

        if (config.getAdminUsername().isPresent() || config.getAdminPassword().isPresent()) {
            adminHandler.setSecurityHandler(basicAuthHandler(config.getAdminUsername().or(""),
                                                             config.getAdminPassword().or("")));
        }

        final Handler handler = createHandler(applicationConnector, adminConnector);
        if (config.getRequestLogConfiguration().getOutputs().isEmpty()) {
            server.setHandler(handler);
        } else {
            final RequestLogHandler requestLogHandler = new RequestLogHandler();
            requestLogHandler.setHandler(handler);
            requestLogHandler.setRequestLog(buildRequestLog());
            server.setHandler(requestLogHandler);
        }

        // TODO: 4/13/13 <coda> -- fix Jetty error handling
        server.addBean(new ErrorHandler());

        server.setStopAtShutdown(true);

        return server;
    }

    private SecurityHandler basicAuthHandler(String username, String password) {
        final HashLoginService loginService = new HashLoginService();
        loginService.putUser(username, Credential.getCredential(password), new String[]{ "user" });
        loginService.setName("admin");

        final Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{ "user" });
        constraint.setAuthenticate(true);

        final ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        final ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName("admin");
        csh.addConstraintMapping(constraintMapping);
        csh.setLoginService(loginService);

        return csh;
    }

    private Handler createRoutingHandler(Connector applicationConnector,
                                         Connector adminConnector) {
        // if we're on the same connector, route by context path
        if (applicationConnector == adminConnector) {
            return new ContextRoutingHandler(applicationHandler, adminHandler);
        }

        // otherwise, route by connector
        return new RoutingHandler(ImmutableMap.<Connector, Handler>of(
                applicationConnector, applicationHandler,
                adminConnector, adminHandler
        ));
    }

    private Connector createAdminConnector(Server server) {
        final QueuedThreadPool threadPool = new QueuedThreadPool(16, 1);
        threadPool.setName("dw-admin");

        final ServerConnector connector = new ServerConnector(server, threadPool, null, null, 1, 1,
                                                              new HttpConnectionFactory());
        connector.setHost(config.getBindHost().orNull());
        connector.setPort(config.getAdminPort());
        connector.setName("admin");

        return connector;
    }

    private Connector createApplicationConnector(Server server) {
        final HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setHeaderCacheSize((int) config.getHeaderCacheSize().toBytes());
        httpConfig.setOutputBufferSize((int) config.getOutputBufferSize().toBytes());
        httpConfig.setRequestHeaderSize((int) config.getMaxRequestHeaderSize().toBytes());
        httpConfig.setResponseHeaderSize((int) config.getMaxResponseHeaderSize().toBytes());
        httpConfig.setSendDateHeader(config.isDateHeaderEnabled());
        httpConfig.setSendServerVersion(config.isServerHeaderEnabled());

        if (config.useForwardedHeaders()) {
            httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        }

        final HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfig);
        httpConnectionFactory.setInputBufferSize((int) config.getInputBufferSize().toBytes());

        final Scheduler scheduler = new ScheduledExecutorScheduler();


        final ByteBufferPool bufferPool = config.useDirectBuffers() ?
                new MappedByteBufferPool() :
                new ArrayByteBufferPool((int) config.getMinBufferPoolSize().toBytes(),
                                        (int) config.getBufferPoolIncrement().toBytes(),
                                        (int) config.getMaxBufferPoolSize().toBytes());

        final ServerConnector connector = new ServerConnector(server,
                                                              null,
                                                              scheduler,
                                                              bufferPool,
                                                              config.getAcceptorThreads(),
                                                              config.getSelectorThreads(),
                                                              httpConnectionFactory);
        connector.setPort(config.getPort());
        connector.setHost(config.getBindHost().orNull());
        connector.setAcceptQueueSize(config.getAcceptQueueSize());
        connector.setReuseAddress(config.isReuseAddressEnabled());
        for (Duration linger : config.getSoLingerTime().asSet()) {
            connector.setSoLingerTime((int) linger.toSeconds());
        }
        connector.setIdleTimeout(config.getIdleTimeout().toMilliseconds());
        connector.setName("application");
        return connector;
    }

    private ThreadPool createThreadPool() {
        final BlockingQueue<Runnable> queue = new BlockingArrayQueue<>(config.getMinThreads(),
                                                                       config.getMaxThreads(),
                                                                       config.getMaxQueuedRequests()
                                                                             .or(Integer.MAX_VALUE));
        final QueuedThreadPool pool = new QueuedThreadPool(config.getMaxThreads(),
                                                           config.getMinThreads(),
                                                           60000,
                                                           queue);
        pool.setName("dw");
        return pool;
    }

    private Handler createHandler(Connector applicationConnector, Connector adminConnector) {
        final Handler handler = createRoutingHandler(applicationConnector, adminConnector);

        // TODO: 4/15/13 <coda> -- re-add instrumentation
//        final InstrumentedHandler instrumented = new InstrumentedHandler(handler);
        final GzipConfiguration gzip = config.getGzipConfiguration();
        if (gzip.isEnabled()) {
            final BiDiGzipHandler gzipHandler = new BiDiGzipHandler(handler);

            final Size minEntitySize = gzip.getMinimumEntitySize();
            gzipHandler.setMinGzipSize((int) minEntitySize.toBytes());

            final Size bufferSize = gzip.getBufferSize();
            gzipHandler.setBufferSize((int) bufferSize.toBytes());

            final ImmutableSet<String> userAgents = gzip.getExcludedUserAgents();
            if (!userAgents.isEmpty()) {
                gzipHandler.setExcluded(userAgents);
            }

            final ImmutableSet<String> mimeTypes = gzip.getCompressedMimeTypes();
            if (!mimeTypes.isEmpty()) {
                gzipHandler.setMimeTypes(mimeTypes);
            }

            return gzipHandler;
        }

        return handler;
    }

    private RequestLog buildRequestLog() {
        final Logger logger = (Logger) LoggerFactory.getLogger("http.request");
        logger.setAdditive(false);
        logger.setLevel(Level.INFO);
        final LoggerContext context = logger.getLoggerContext();

        final AppenderAttachableImpl<ILoggingEvent> appenders = new AppenderAttachableImpl<>();

        final RequestLogLayout layout = new RequestLogLayout();
        layout.start();

        for (LoggingOutput output : config.getRequestLogConfiguration().getOutputs()) {
            appenders.addAppender(output.build(context, name, layout));
        }

        return new AsyncRequestLog(Clock.defaultClock(),
                                   appenders,
                                   config.getRequestLogConfiguration().getTimeZone());
    }
}

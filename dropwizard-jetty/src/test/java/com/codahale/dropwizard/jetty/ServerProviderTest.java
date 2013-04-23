package com.codahale.dropwizard.jetty;

import com.codahale.dropwizard.logging.LoggingOutput;
import com.google.common.collect.ImmutableList;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ServerProviderTest {
    private final ServerConfiguration config = new ServerConfiguration();
    private final ServletContextHandler appHandler = mock(ServletContextHandler.class);
    private final ServletContextHandler adminHandler = mock(ServletContextHandler.class);
    private final ServerProvider provider = new ServerProvider(config,
                                                               appHandler,
                                                               adminHandler,
                                                               "name");

    @Before
    public void setUp() throws Exception {
        when(appHandler.getContextPath()).thenReturn("/app");
        when(adminHandler.getContextPath()).thenReturn("/admin");
    }

    @Test
    public void wrapsTheHandlers() throws Exception {
        final Server server = provider.get();

        assertThat(server.getHandler())
                .isInstanceOf(RequestLogHandler.class);

        final RequestLogHandler requestLogHandler = (RequestLogHandler) server.getHandler();

        assertThat(requestLogHandler.getHandler())
                .isInstanceOf(BiDiGzipHandler.class);

        final BiDiGzipHandler gzipHandler = (BiDiGzipHandler) requestLogHandler.getHandler();

        assertThat(gzipHandler.getHandler())
                .isInstanceOf(RoutingHandler.class);

        final RoutingHandler routingHandler = (RoutingHandler) gzipHandler.getHandler();

        assertThat(routingHandler.getBeans())
                .containsOnly(appHandler, adminHandler);
    }

    @Test
    public void doesNotIncludeAGzipHandlerIfGzipIsDisabled() throws Exception {
        config.getGzipConfiguration().setEnabled(false);

        final Server server = provider.get();

        assertThat(server.getHandler())
                .isInstanceOf(RequestLogHandler.class);

        final RequestLogHandler requestLogHandler = (RequestLogHandler) server.getHandler();

        assertThat(requestLogHandler.getHandler())
                .isInstanceOf(RoutingHandler.class);

        final RoutingHandler routingHandler = (RoutingHandler) requestLogHandler.getHandler();

        assertThat(routingHandler.getBeans())
                .containsOnly(appHandler, adminHandler);
    }

    @Test
    public void doesNotIncludeARequestLogHandlerIfRequestLogIsDisabled() throws Exception {
        config.getRequestLogConfiguration().setOutputs(ImmutableList.<LoggingOutput>of());

        final Server server = provider.get();

        assertThat(server.getHandler())
                .isInstanceOf(BiDiGzipHandler.class);

        final BiDiGzipHandler gzipHandler = (BiDiGzipHandler) server.getHandler();

        assertThat(gzipHandler.getHandler())
                .isInstanceOf(RoutingHandler.class);

        final RoutingHandler routingHandler = (RoutingHandler) gzipHandler.getHandler();

        assertThat(routingHandler.getBeans())
                .containsOnly(appHandler, adminHandler);
    }

    @Test
    public void usesAContextRoutingHandlerIfPortAndAdminPortAreTheSame() throws Exception {
        config.setPort(8080);
        config.setAdminPort(8080);

        final Server server = provider.get();

        assertThat(server.getHandler())
                .isInstanceOf(RequestLogHandler.class);

        final RequestLogHandler requestLogHandler = (RequestLogHandler) server.getHandler();

        assertThat(requestLogHandler.getHandler())
                .isInstanceOf(BiDiGzipHandler.class);

        final BiDiGzipHandler gzipHandler = (BiDiGzipHandler) requestLogHandler.getHandler();

        assertThat(gzipHandler.getHandler())
                .isInstanceOf(ContextRoutingHandler.class);

        final ContextRoutingHandler routingHandler = (ContextRoutingHandler) gzipHandler.getHandler();

        assertThat(routingHandler.getBeans())
                .containsOnly(appHandler, adminHandler);
    }

    @Test
    public void addsASecurityHandlerForTheAdminHandlerIfUsernameAndPasswordAreEnabled() throws Exception {
        config.setAdminUsername("one");
        config.setAdminPassword("two");

        provider.get();

        final ArgumentCaptor<SecurityHandler> captor = ArgumentCaptor.forClass(SecurityHandler.class);
        verify(adminHandler).setSecurityHandler(captor.capture());

        final SecurityHandler handler = captor.getValue();
        assertThat(handler)
                .isInstanceOf(ConstraintSecurityHandler.class);
    }
}

package com.yammer.dropwizard.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class ContextRoutingHandlerTest {
    private final Request baseRequest = mock(Request.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);

    private final ContextHandler handler1 = mock(ContextHandler.class);
    private final ContextHandler handler2 = mock(ContextHandler.class);

    private ContextRoutingHandler handler;

    @Before
    public void setUp() throws Exception {
        when(handler1.getContextPath()).thenReturn("/");
        when(handler2.getContextPath()).thenReturn("/admin");

        this.handler = new ContextRoutingHandler(handler1, handler2);
    }

    @Test
    public void routesToTheBestPrefixMatch() throws Exception {
        when(baseRequest.getRequestURI()).thenReturn("/hello-world");

        handler.handle("/hello-world", baseRequest, request, response);

        verify(handler1).handle("/hello-world", baseRequest, request, response);
        verify(handler2, never()).handle("/hello-world", baseRequest, request, response);
    }

    @Test
    public void passesHandlingNonMatchingRequests() throws Exception {
        when(baseRequest.getRequestURI()).thenReturn("WAT");

        handler.handle("WAT", baseRequest, request, response);

        verify(handler1, never()).handle("WAT", baseRequest, request, response);
        verify(handler2, never()).handle("WAT", baseRequest, request, response);
    }
}

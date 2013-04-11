package com.yammer.dropwizard.jetty;

import com.google.common.collect.ImmutableMap;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RoutingHandler extends AbstractHandler {
    private final ImmutableMap<Connector, Handler> handlers;

    public RoutingHandler(Map<Connector, Handler> handlers) {
        this.handlers = ImmutableMap.copyOf(handlers);
        for (Handler handler : handlers.values()) {
            addBean(handler);
        }
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        handlers.get(baseRequest.getHttpChannel().getConnector())
                .handle(target, baseRequest, request, response);
    }
}


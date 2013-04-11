package com.yammer.dropwizard.jetty;

import com.google.inject.Injector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class ServletContextHandlerProvider implements Provider<ServletContextHandler> {
    private final Injector injector;
    private final List<FilterBuilder> filters;
    private final List<ServletBuilder> servlets;

    @Inject
    public ServletContextHandlerProvider(Injector injector,
                                         List<FilterBuilder> filters,
                                         List<ServletBuilder> servlets) {
        this.injector = injector;
        this.filters = filters;
        this.servlets = servlets;
    }

    @Override
    public ServletContextHandler get() {
        final ServletContextHandler handler = new ServletContextHandler();
        addFilters(handler);
        return handler;
    }

    private void addFilters(ServletContextHandler handler) {
        for (FilterBuilder builder : filters) {
            final FilterHolder holder = builder.getHolder();
            if (holder.getFilter() == null && holder.getHeldClass() != null) {
                holder.setFilter(injector.getInstance(holder.getHeldClass()));
            }

            for (String path : builder.getPaths()) {
                handler.addFilter(holder, path, builder.getDispatchers());
            }
        }

        for (ServletBuilder builder : servlets) {
            final ServletHolder holder = builder.getHolder();
            if (holder.getServletInstance() == null && holder.getHeldClass() != null) {
                holder.setServlet(injector.getInstance(holder.getHeldClass()));
            }

            for (String path : builder.getPaths()) {
                handler.addServlet(holder, path);
            }
        }
    }
}

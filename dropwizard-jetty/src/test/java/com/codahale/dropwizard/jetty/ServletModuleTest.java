package com.codahale.dropwizard.jetty;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.jetty.servlet.*;
import org.junit.Test;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ServletModuleTest {
    private static class ExampleServlet extends HttpServlet {
        private final String value;

        @Inject
        private ExampleServlet(String value) {
            this.value = value;
        }

        private String getValue() {
            return value;
        }
    }

    private static class ExampleFilter implements Filter {
        private final String value;

        @Inject
        private ExampleFilter(String value) {
            this.value = value;
        }

        private String getValue() {
            return value;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        }

        @Override
        public void destroy() {
        }
    }

    private final Injector injector = Guice.createInjector(new ServletModule() {
        @Override
        protected void configureServlets() {
            bind(String.class).toInstance("injected");

            filter("/hunk/*")
                    .through(new ExampleFilter("manual"))
                    .dispatcher(DispatcherType.REQUEST)
                    .initParam("one", "two");

            filter("/*").through(ExampleFilter.class).named("example-filter");

            serve("/manual")
                    .with(new ExampleServlet("manual"))
                    .initOrder(2)
                    .initParam("name", "value")
                    .runAsRole("role")
                    .userLink("name", "link");

            serve("/*").with(ExampleServlet.class).named("example");
        }
    });

    @Test
    public void buildsASingletonServletContextHandler() throws Exception {
        assertThat(injector.getInstance(ServletContextHandler.class))
                .isSameAs(injector.getInstance(ServletContextHandler.class));
    }

    @Test
    public void addFilters() throws Exception {
        final ServletContextHandler context = injector.getInstance(ServletContextHandler.class);

        // sadly, can't test dispatchers w/o just parsing FilterMapping#toString() which is insane
        final FilterMapping mapping = context.getServletHandler().getFilterMappings()[0];
        assertThat(mapping.getPathSpecs())
                .containsOnly("/hunk/*");

        final FilterHolder holder = context.getServletHandler().getFilters()[0];
        assertThat(holder.getFilter())
                .isInstanceOf(ExampleFilter.class);

        assertThat(((ExampleFilter) holder.getFilter()).getValue())
                .isEqualTo("manual");

        assertThat(holder.getInitParameters())
                .isEqualTo(ImmutableMap.of("one", "two"));
    }

    @Test
    public void addFilterClasses() throws Exception {
        final ServletContextHandler context = injector.getInstance(ServletContextHandler.class);

        final FilterMapping mapping = context.getServletHandler().getFilterMappings()[1];
        assertThat(mapping.getPathSpecs())
                .containsOnly("/*");

        final FilterHolder holder = context.getServletHandler().getFilters()[1];
        assertThat(holder.getFilter())
                .isInstanceOf(ExampleFilter.class);

        assertThat(((ExampleFilter) holder.getFilter()).getValue())
                .isEqualTo("injected");

        assertThat(holder.getName())
                .isEqualTo("example-filter");

        assertThat(holder.getDisplayName())
                .isEqualTo("example-filter");
    }

    @Test
    public void addServlets() throws Exception {
        final ServletContextHandler context = injector.getInstance(ServletContextHandler.class);

        final ServletMapping mapping = context.getServletHandler().getServletMappings()[0];
        assertThat(mapping.getPathSpecs())
                .containsOnly("/manual");

        final ServletHolder holder = context.getServletHandler().getServlets()[0];
        assertThat(holder.getServletInstance())
                .isInstanceOf(ExampleServlet.class);
        assertThat(((ExampleServlet) holder.getServletInstance()).getValue())
                .isEqualTo("manual");
        assertThat(holder.getInitOrder())
                .isEqualTo(2);
        assertThat(holder.getInitParameters())
                .isEqualTo(ImmutableMap.of("name", "value"));
        assertThat(holder.getRunAsRole())
                .isEqualTo("role");
        assertThat(holder.getUserRoleLink("name"))
                .isEqualTo("link");
    }

    @Test
    public void addServletClasses() throws Exception {
        final ServletContextHandler context = injector.getInstance(ServletContextHandler.class);

        final ServletMapping mapping = context.getServletHandler().getServletMappings()[1];
        assertThat(mapping.getPathSpecs())
                .containsOnly("/*");

        final ServletHolder holder = context.getServletHandler().getServlets()[1];
        assertThat(holder.getServletInstance())
                .isInstanceOf(ExampleServlet.class);
        assertThat(((ExampleServlet) holder.getServletInstance()).getValue())
                .isEqualTo("injected");

        assertThat(holder.getName())
                .isEqualTo("example");

        assertThat(holder.getDisplayName())
                .isEqualTo("example");
    }
}

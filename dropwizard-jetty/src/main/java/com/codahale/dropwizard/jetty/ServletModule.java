package com.codahale.dropwizard.jetty;

import com.google.common.collect.Lists;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.codahale.dropwizard.guice.PrivateDropwizardModule;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class ServletModule extends PrivateDropwizardModule {
    private final List<FilterBuilder> filters;
    private final List<ServletBuilder> servlets;

    protected ServletModule() {
        this(null, null);
    }

    protected ServletModule(String name) {
        this(Names.named(name));
    }

    protected ServletModule(Annotation annotation) {
        this(annotation, null);
    }

    protected ServletModule(Class<? extends Annotation> annotationType) {
        this(null, annotationType);
    }

    private ServletModule(Annotation annotation, Class<? extends Annotation> annotationType) {
        super(annotation, annotationType);
        this.filters = Lists.newArrayList();
        this.servlets = Lists.newArrayList();
    }

    @Override
    protected final void configure() {
        bind(new TypeLiteral<List<FilterBuilder>>() {}).toInstance(filters);
        bind(new TypeLiteral<List<ServletBuilder>>() {}).toInstance(servlets);

        configureServlets();

        bindAndExpose(ServletContextHandler.class)
                .toProvider(ServletContextHandlerProvider.class)
                .in(Scopes.SINGLETON);

    }

    protected abstract void configureServlets();

    protected FilterBuilder filter(String pathSpec, String... otherPaths) {
        final FilterBuilder builder = new FilterBuilder(pathSpec, otherPaths);
        filters.add(builder);
        return builder;
    }

    protected ServletBuilder serve(String pathSpec, String... otherPaths) {
        final ServletBuilder builder = new ServletBuilder(pathSpec, otherPaths);
        servlets.add(builder);
        return builder;
    }
}

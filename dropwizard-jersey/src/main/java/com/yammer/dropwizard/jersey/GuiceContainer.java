package com.yammer.dropwizard.jersey;

import com.google.inject.Injector;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import javax.inject.Inject;

public class GuiceContainer extends ServletContainer {
    private final ResourceConfig config;
    private final Injector injector;

    @Inject
    public GuiceContainer(ResourceConfig config,
                          Injector injector) {
        super(config);
        this.config = config;
        this.injector = injector;
    }

    @Override
    protected void initiate(ResourceConfig rc, WebApplication wa) {
        wa.initiate(rc, new GuiceComponentProviderFactory(rc, injector));
    }

    public ResourceConfig getConfig() {
        return config;
    }
}


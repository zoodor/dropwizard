package com.codahale.dropwizard.jersey;

import com.google.inject.Injector;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GuiceContainerTest {
    private final ResourceConfig config = mock(ResourceConfig.class);
    private final Injector injector = mock(Injector.class);

    private final GuiceContainer container = new GuiceContainer(config, injector);

    @Test
    public void initiatesTheWebApplicationWithAGuiceComponentProviderFactory() throws Exception {
        final WebApplication webApp = mock(WebApplication.class);

        container.initiate(config, webApp);

        verify(webApp).initiate(eq(config), any(GuiceComponentProviderFactory.class));
    }
}

package com.codahale.dropwizard.lifecycle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class LifecycleModuleTest {
    @Singleton
    private static class Example extends AbstractLifeCycle {

    }

    private final Injector injector = Guice.createInjector(new LifecycleModule() {
        @Override
        protected void configureLifecycles() {
            bindLifecycle(Example.class);
        }
    });

    private final ContainerLifeCycle container = new ContainerLifeCycle();

    @Test
    public void bindsALifecycleManager() throws Exception {
        final LifecycleManager manager = injector.getInstance(LifecycleManager.class);
        manager.attach(container);
        container.start();

        assertThat(injector.getInstance(Example.class).isStarted())
                .isTrue();

        container.stop();

        assertThat(injector.getInstance(Example.class).isStopped())
                .isTrue();
    }

    @Test
    public void createsASingletonLifecycleManager() throws Exception {
        assertThat(injector.getInstance(LifecycleManager.class))
                .isSameAs(injector.getInstance(LifecycleManager.class));
    }
}

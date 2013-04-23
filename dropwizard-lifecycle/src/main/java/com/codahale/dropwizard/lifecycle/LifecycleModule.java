package com.codahale.dropwizard.lifecycle;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import org.eclipse.jetty.util.component.LifeCycle;

public abstract class LifecycleModule extends AbstractModule {
    @Override
    protected final void configure() {
        bind(LifecycleManager.class).in(Scopes.SINGLETON);
        configureLifecycles();
    }

    protected abstract void configureLifecycles();

    protected void bindLifecycle(Class<? extends LifeCycle> klass) {
        Multibinder.newSetBinder(binder(), LifeCycle.class).permitDuplicates().addBinding().to(klass);
    }
}

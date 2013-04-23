package com.codahale.dropwizard.lifecycle;

import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.LifeCycle;

import javax.inject.Inject;
import java.util.Set;

public class LifecycleManager {
    private final Set<LifeCycle> managedObjects;

    @Inject
    private LifecycleManager(Set<LifeCycle> managedObjects) {
        this.managedObjects = managedObjects;
    }

    public void attach(Container container) {
        for (LifeCycle object : managedObjects) {
            container.addBean(object);
        }
    }
}

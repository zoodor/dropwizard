package com.yammer.dropwizard.jersey;

import com.sun.jersey.api.core.ScanningResourceConfig;
import com.yammer.dropwizard.jersey.caching.CacheControlledResourceMethodDispatchAdapter;
import com.yammer.dropwizard.jersey.errors.ConstraintViolationExceptionMapper;
import com.yammer.dropwizard.jersey.errors.JacksonExceptionMapper;
import com.yammer.dropwizard.jersey.errors.LoggingExceptionMapper;
import com.yammer.metrics.jersey.InstrumentedResourceMethodDispatchAdapter;

public class DropwizardResourceConfig extends ScanningResourceConfig {
    public DropwizardResourceConfig(boolean testOnly) {
        super();
        getFeatures().put(FEATURE_DISABLE_WADL, Boolean.TRUE);
        if (!testOnly) {
            // create a subclass to pin it to Throwable
            getSingletons().add(new LoggingExceptionMapper<Throwable>() {});
            getSingletons().add(new ConstraintViolationExceptionMapper());
            getSingletons().add(new JacksonExceptionMapper());
        }
        getClasses().add(InstrumentedResourceMethodDispatchAdapter.class);
        getClasses().add(CacheControlledResourceMethodDispatchAdapter.class);
        getClasses().add(OptionalResourceMethodDispatchAdapter.class);
        getClasses().add(OptionalQueryParamInjectableProvider.class);
    }
}

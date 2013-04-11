package com.yammer.dropwizard.validation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import javax.validation.ValidatorFactory;

import static org.fest.assertions.api.Assertions.assertThat;

public class ValidationModuleTest {
    private final Injector injector = Guice.createInjector(new ValidationModule());

    @Test
    public void providesASingletonValidatorFactoryInstance() throws Exception {
        assertThat(injector.getInstance(ValidatorFactory.class))
                .isSameAs(injector.getInstance(ValidatorFactory.class));
    }
}

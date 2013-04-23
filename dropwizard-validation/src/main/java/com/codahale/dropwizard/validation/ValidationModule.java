package com.codahale.dropwizard.validation;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidationModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public ValidatorFactory provideValidatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }

    @Provides
    public Validator provideValidator(ValidatorFactory factory) {
        return factory.getValidator();
    }
}

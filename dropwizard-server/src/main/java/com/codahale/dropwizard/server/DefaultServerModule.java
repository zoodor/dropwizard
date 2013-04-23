package com.codahale.dropwizard.server;

import com.codahale.dropwizard.config.FileSourceProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.codahale.dropwizard.config.SourceProvider;
import com.codahale.dropwizard.jackson.JacksonModule;
import com.codahale.dropwizard.jetty.ServerProvider;
import com.codahale.dropwizard.util.JarLocation;
import org.eclipse.jetty.server.Server;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.PrintWriter;

public class DefaultServerModule extends AbstractModule {
    private final Application<?> application;

    public DefaultServerModule(Application<?> application) {
        this.application = application;
    }

    @Override
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    protected void configure() {
        bind(Validator.class).toInstance(Validation.buildDefaultValidatorFactory()
                                                   .getValidator());
        bind(SourceProvider.class).to(FileSourceProvider.class);
        bind(new TypeLiteral<Class<?>>() {}).toInstance(application.getConfigurationClass());
        bind(JarLocation.class).toInstance(new JarLocation(application.getConfigurationClass()));
        bind(PrintWriter.class).toInstance(new PrintWriter(System.out));
        bind(Server.class).toProvider(ServerProvider.class).in(Scopes.SINGLETON);
        bind(Key.get(String.class, Names.named("application"))).toInstance(application.getName());
        install(new JacksonModule());
    }
}

package com.yammer.dropwizard.server;

import com.google.common.collect.Lists;
import com.google.inject.*;
import com.google.inject.util.Modules;
import com.yammer.dropwizard.cli.Command;
import com.yammer.dropwizard.cli.CommandLineParser;
import com.yammer.dropwizard.cli.CommandModule;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.config.SourceProvider;
import com.yammer.dropwizard.jetty.ServerConfiguration;
import com.yammer.dropwizard.logging.Logging;
import com.yammer.dropwizard.logging.LoggingFactory;
import com.yammer.dropwizard.server.commands.ServerCommand;
import com.yammer.dropwizard.util.Generics;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class Application<T extends Configuration> extends AbstractModule {
    static {
        // make sure we don't get nagged by Hibernate Validator and others
        Logging.bootstrap();
    }

    private final Module defaults;
    private final List<Module> modules;
    private volatile T configuration;

    protected Application() {
        this.modules = Lists.newArrayList();
        final Application<?> app = this;
        this.defaults = new DefaultServerModule(app);
    }

    protected abstract void setup();

    @Override
    protected final void configure() {

    }

    @Provides
    public final T getConfiguration(Namespace namespace,
                                    ConfigurationFactory<T> factory,
                                    SourceProvider sourceProvider) throws IOException, ConfigurationException {
        // build our own singleton here so's we don't accidentally poop
        // because the default config is invalid but we eagerly loaded the damn
        // thing
        if (configuration == null) {
            final String path = namespace.getString("file");
            if (path == null) {
                this.configuration = factory.build();
            } else {
                try (InputStream input = sourceProvider.create(path)) {
                    this.configuration = factory.build(path, input);
                }
            }

            // dat loggin'
            new LoggingFactory(configuration.getLoggingConfiguration(), getName()).configure();
        }

        return configuration;
    }

    @Provides
    public final ServerConfiguration getServerConfiguration(T configuration) {
        return configuration.getServerConfiguration();
    }

    public final Class<T> getConfigurationClass() {
        return Generics.getTypeParameter(getClass(), Configuration.class);
    }

    public abstract String getName();

    public void run(final String... args) throws Exception {
        setup();
        final AbstractModule commandLine = new CommandModule(args) {
            @Override
            protected void configureCommands() {
                addCommand(ServerCommand.class);
            }

            @Provides
            public Namespace getNamespace(CommandLineParser.Invocation invocation) {
                return invocation.getNamespace();
            }
        };

        // TODO: 4/18/13 <coda> -- add error handling

        Guice.createInjector(Modules.override(defaults).with(modules), this, commandLine)
             .getInstance(Command.class).run();
    }

    protected void addModule(Module module) {
        modules.add(module);
    }
}

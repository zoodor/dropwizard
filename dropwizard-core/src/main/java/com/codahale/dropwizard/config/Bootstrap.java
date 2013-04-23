package com.codahale.dropwizard.config;

import com.codahale.dropwizard.Bundle;
import com.codahale.dropwizard.ConfiguredBundle;
import com.codahale.dropwizard.Service;
import com.codahale.dropwizard.cli.Command;
import com.codahale.dropwizard.cli.ConfiguredCommand;
import com.codahale.dropwizard.json.ObjectMapperFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class Bootstrap<T extends Configuration> {
    private final Service<T> service;
    private SourceProvider configurationProvider = new FileSourceProvider();
    private final ObjectMapperFactory objectMapperFactory;
    private final List<Bundle> bundles;
    private final List<ConfiguredBundle<? super T>> configuredBundles;
    private final List<Command> commands;

    public Bootstrap(Service<T> service) {
        this.service = service;
        this.objectMapperFactory = new ObjectMapperFactory();
        this.bundles = Lists.newArrayList();
        this.configuredBundles = Lists.newArrayList();
        this.commands = Lists.newArrayList();
    }

    public Service<T> getService() {
        return service;
    }

    public SourceProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public void setConfigurationProvider(SourceProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public void addBundle(Bundle bundle) {
        bundle.initialize(this);
        bundles.add(bundle);
    }

    public void addBundle(ConfiguredBundle<? super T> bundle) {
        bundle.initialize(this);
        configuredBundles.add(bundle);
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void addCommand(ConfiguredCommand<T> command) {
        commands.add(command);
    }

    public ObjectMapperFactory getObjectMapperFactory() {
        return objectMapperFactory;
    }

    public void runWithBundles(T configuration, Environment environment) throws Exception {
        for (Bundle bundle : bundles) {
            bundle.run(environment);
        }
        for (ConfiguredBundle<? super T> bundle : configuredBundles) {
            bundle.run(configuration, environment);
        }
    }

    public ImmutableList<Command> getCommands() {
        return ImmutableList.copyOf(commands);
    }
}

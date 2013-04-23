package com.codahale.dropwizard.cli;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Provider;

public class CommandProvider implements Provider<Command> {
    private final CommandLineParser.Invocation invocation;
    private final Injector injector;

    @Inject
    public CommandProvider(CommandLineParser.Invocation invocation, Injector injector) {
        this.invocation = invocation;
        this.injector = injector;
    }

    @Override
    public Command get() {
        return injector.getInstance(invocation.getCommandClass());
    }
}

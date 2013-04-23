package com.codahale.dropwizard.cli;

import javax.inject.Inject;
import javax.inject.Provider;

public class InvocationProvider implements Provider<CommandLineParser.Invocation> {
    private final CommandLineParser parser;
    private final String[] args;

    @Inject
    public InvocationProvider(CommandLineParser parser, String... args) {
        this.parser = parser;
        this.args = args;
    }

    @Override
    public CommandLineParser.Invocation get() {
        return parser.parse(args).get();
    }
}

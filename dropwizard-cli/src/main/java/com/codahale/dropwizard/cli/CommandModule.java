package com.codahale.dropwizard.cli;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

import java.util.Arrays;

public abstract class CommandModule extends AbstractModule {
    private final String[] arguments;

    protected CommandModule(String... arguments) {
        this.arguments = Arrays.copyOf(arguments, arguments.length);
    }

    @Override
    protected final void configure() {
        bind(String[].class).toInstance(arguments);
        bind(Command.class).toProvider(CommandProvider.class);
        bind(CommandLineParser.Invocation.class).toProvider(InvocationProvider.class);

        configureCommands();
    }

    protected abstract void configureCommands();

    protected void addCommand(Class<? extends Command> klass) {
        final Optional<Command.Description> description = Command.Description.forClass(klass);
        if (!description.isPresent()) {
            throw new IllegalArgumentException(klass.getName() + " is not annotated with @Command.Info");
        }

        final Command.Description desc = description.get();

        Multibinder.newSetBinder(binder(), Command.Description.class)
                   .addBinding()
                   .toInstance(desc);
        MapBinder.newMapBinder(binder(), String.class, Command.Params.class)
                 .addBinding(desc.getInfo().name())
                 .to(desc.getInfo().params()).in(Scopes.SINGLETON);
    }
}

package com.codahale.dropwizard.cli;

import com.google.inject.Injector;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CommandProviderTest {
    private static class ExampleCommand implements Command {
        @Override
        public void run() throws Exception {
        }
    }

    private final Command command = mock(ExampleCommand.class);
    private final CommandLineParser.Invocation invocation = mock(CommandLineParser.Invocation.class);
    private final Injector injector = mock(Injector.class);
    private final CommandProvider provider = new CommandProvider(invocation, injector);

    @Test
    public void providesAnInstanceOfTheInvocationsCommandClass() throws Exception {
        doReturn(ExampleCommand.class).when(invocation).getCommandClass();
        doReturn(command).when(injector).getInstance(ExampleCommand.class);

        assertThat(provider.get())
                .isEqualTo(command);
    }
}

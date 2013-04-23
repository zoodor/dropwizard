package com.codahale.dropwizard.cli;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InvocationProviderTest {
    private final CommandLineParser parser = mock(CommandLineParser.class);
    private final CommandLineParser.Invocation invocation = mock(CommandLineParser.Invocation.class);
    private final InvocationProvider provider = new InvocationProvider(parser, "one", "two");

    @Test
    public void parsesTheCommandLine() throws Exception {
        doReturn(Optional.of(invocation)).when(parser).parse("one", "two");

        assertThat(provider.get())
                .isEqualTo(invocation);

        verify(parser).parse("one", "two");
    }
}

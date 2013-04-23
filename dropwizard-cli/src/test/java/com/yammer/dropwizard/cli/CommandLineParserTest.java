package com.yammer.dropwizard.cli;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yammer.dropwizard.util.JarLocation;
import org.junit.Test;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

public class CommandLineParserTest {
    @Command.Info(name = "one",
                  description = "first command")
    private static class OneCommand implements Command {
        private final Runnable runnable;

        @Inject
        private OneCommand(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() throws Exception {
            runnable.run();
        }
    }

    @Command.Info(name = "two",
                  description = "second command",
                  params = Command.Params.ConfigFile.class)
    private static class TwoCommand implements Command {
        private final Runnable runnable;

        @Inject
        private TwoCommand(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() throws Exception {
            runnable.run();
            runnable.run();
        }
    }

    private final StringWriter output = new StringWriter();
    private final PrintWriter writer = new PrintWriter(output);
    private final Injector injector = Guice.createInjector(new CommandModule() {
        @Override
        protected void configureCommands() {
            bind(JarLocation.class).toInstance(new JarLocation(getClass()));
            bind(PrintWriter.class).toInstance(writer);
            addCommand(OneCommand.class);
            addCommand(TwoCommand.class);
        }
    });

    private final CommandLineParser parser = injector.getInstance(CommandLineParser.class);

    @Test
    public void considersNoArgumentsToBeAHelpRequest() throws Exception {
        final Optional<CommandLineParser.Invocation> invocation = parser.parse();

        assertThat(invocation.isPresent())
                .isFalse();

        assertThat(output.toString())
                .isEqualTo(
                        "usage: java -jar project.jar [-h] [-v] {one,two} ...\n" +
                                '\n' +
                                "positional arguments:\n" +
                                "  {one,two}              available commands\n" +
                                '\n' +
                                "optional arguments:\n" +
                                "  -h, --help             show this help message and exit\n" +
                                "  -v, --version          show the application version and exit\n"
                );
    }

    @Test
    public void considersDashHToBeAHelpRequest() throws Exception {
        final Optional<CommandLineParser.Invocation> invocation = parser.parse("-h");

        assertThat(invocation.isPresent())
                .isFalse();

        assertThat(output.toString())
                .isEqualTo(
                        "usage: java -jar project.jar [-h] [-v] {one,two} ...\n" +
                                '\n' +
                                "positional arguments:\n" +
                                "  {one,two}              available commands\n" +
                                '\n' +
                                "optional arguments:\n" +
                                "  -h, --help             show this help message and exit\n" +
                                "  -v, --version          show the application version and exit\n"
                );
    }

    @Test
    public void considersDashDashHelpToBeAHelpRequest() throws Exception {
        final Optional<CommandLineParser.Invocation> invocation = parser.parse("--help");

        assertThat(invocation.isPresent())
                .isFalse();

        assertThat(output.toString())
                .isEqualTo(
                        "usage: java -jar project.jar [-h] [-v] {one,two} ...\n" +
                                '\n' +
                                "positional arguments:\n" +
                                "  {one,two}              available commands\n" +
                                '\n' +
                                "optional arguments:\n" +
                                "  -h, --help             show this help message and exit\n" +
                                "  -v, --version          show the application version and exit\n"
                );
    }

    @Test
    public void considersDashVAVersionRequest() throws Exception {
        final Optional<CommandLineParser.Invocation> invocation = parser.parse("-v");

        assertThat(invocation.isPresent())
                .isFalse();

        assertThat(output.toString())
                .isEqualTo("No service version detected. Add a Implementation-Version entry to your JAR's manifest to enable this.\n");
    }

    @Test
    public void considersDashDashVersionAVersionRequest() throws Exception {
        final Optional<CommandLineParser.Invocation> invocation = parser.parse("--version");

        assertThat(invocation.isPresent())
                .isFalse();

        assertThat(output.toString())
                .isEqualTo(
                        "No service version detected. Add a Implementation-Version entry to your JAR's manifest to enable this.\n");
    }

    @Test
    public void automaticallyHandlesHelpActions() throws Exception {
        final Optional<CommandLineParser.Invocation> invocation = parser.parse("one", "-h");

        assertThat(invocation.isPresent())
                .isFalse();

        assertThat(output.toString())
                .isEqualTo(
                        "usage: java -jar project.jar one [-h]\n" +
                                '\n' +
                                "first command\n" +
                                '\n' +
                                "optional arguments:\n" +
                                "  -h, --help             show this help message and exit\n"
                );
    }

    @Test
    public void automaticallyHandlesErrors() throws Exception {
        final Optional<CommandLineParser.Invocation> invocation = parser.parse("one", "--two");

        assertThat(invocation.isPresent())
                .isFalse();

        assertThat(output.toString())
                .isEqualTo(
                        "unrecognized arguments: '--two'\n"+
                                "usage: java -jar project.jar one [-h]\n" +
                                '\n' +
                                "first command\n" +
                                '\n' +
                                "optional arguments:\n" +
                                "  -h, --help             show this help message and exit\n"
                );
    }

    @Test
    public void parsesArgumentsToInvocations() throws Exception {
        final Optional<CommandLineParser.Invocation> optional = parser.parse("two", "filename");

        assertThat(optional.isPresent())
                .isTrue();

        final CommandLineParser.Invocation invocation = optional.get();

        assertThat((Object) invocation.getCommandClass())
                .isEqualTo(TwoCommand.class);

        assertThat(invocation.getNamespace().getAttrs())
                .hasSize(3)
                .contains(entry("file", "filename"))
                .contains(entry("command", "two"))
                .contains(entry("version", null));
    }
}

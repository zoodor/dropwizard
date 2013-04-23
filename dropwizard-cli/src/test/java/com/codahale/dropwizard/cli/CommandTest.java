package com.codahale.dropwizard.cli;

import com.google.common.base.Optional;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Subparser;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CommandTest {
    public interface BadCommand extends Command {

    }

    @Command.Info(name = "example",
                  description = "does a thing",
                  params = Command.Params.ConfigFile.class)
    public static class ExampleCommand implements Command {
        @Override
        public void run() throws Exception {
        }
    }

    @Test
    public void configFileParamsAddAnOptionalFileArgument() throws Exception {
        final ArgumentParser parser = ArgumentParsers.newArgumentParser("whee");
        final Command.Params params = new Command.Params.ConfigFile();
        final Subparser subparser = parser.addSubparsers().addParser("funk");
        params.configure(subparser);

        assertThat(subparser.formatHelp())
                .isEqualTo(
                        "usage: whee funk [-h] [file]\n" +
                                '\n' +
                                "positional arguments:\n" +
                                "  file                   application configuration file\n" +
                                '\n' +
                                "optional arguments:\n" +
                                "  -h, --help             show this help message and exit\n");
    }

    @Test
    public void descriptionsComeFromAnnotations() throws Exception {
        assertThat(Command.Description.forClass(BadCommand.class))
                .isEqualTo(Optional.<Command.Description>absent());

        final Optional<Command.Description> oDesc = Command.Description.forClass(ExampleCommand.class);

        assertThat(oDesc.isPresent())
                .isTrue();

        final Command.Description description = oDesc.get();
        assertThat((Object) description.getCommandClass())
                .isEqualTo(ExampleCommand.class);

        assertThat(description.getInfo().name())
                .isEqualTo("example");

        assertThat(description.getInfo().description())
                .isEqualTo("does a thing");

        assertThat((Object) description.getInfo().params())
                .isEqualTo(Command.Params.ConfigFile.class);
    }
}

package com.codahale.dropwizard.cli;

import com.codahale.dropwizard.util.JarLocation;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class CommandLineParser {
    private static class HelpAction implements ArgumentAction {
        private final PrintWriter output;

        private HelpAction(PrintWriter output) {
            this.output = output;
        }

        @Override
        public void run(ArgumentParser parser, Argument arg, Map<String, Object> attrs, String flag, Object value) throws ArgumentParserException {
            parser.printHelp(output);
            attrs.put("is-internal-action", Boolean.TRUE);
        }

        @Override
        public void onAttach(Argument arg) {
        }

        @Override
        public boolean consumeArgument() {
            return false;
        }
    }

    public static class Invocation {
        private final Namespace namespace;
        private final Class<? extends Command> commandClass;

        public Invocation(Namespace namespace, Class<? extends Command> commandClass) {
            this.namespace = namespace;
            this.commandClass = commandClass;
        }

        public Namespace getNamespace() {
            return namespace;
        }

        public Class<? extends Command> getCommandClass() {
            return commandClass;
        }
    }

    private static final String COMMAND_NAME_ATTR = "command";

    private final ArgumentParser parser;
    private final SortedMap<String, Command.Description> descriptions;
    private final Map<String, Command.Params> params;
    private final PrintWriter output;

    @Inject
    public CommandLineParser(JarLocation jarLocation,
                             Set<Command.Description> descriptions,
                             Map<String, Command.Params> params,
                             PrintWriter output) {
        this.output = output;
        this.parser = buildParser(jarLocation);
        this.descriptions = Maps.newTreeMap();
        this.params = params;
        for (Command.Description description : descriptions) {
            addCommand(description);
        }
    }

    public Optional<Invocation> parse(String... arguments) {
        // assume -h if no arguments are given
        if (arguments.length == 0 || (arguments.length == 1 && ("-h".equals(arguments[0]) || "--help".equals(arguments[0])))) {
            parser.printHelp(output);
            return Optional.absent();
        }

        if (arguments.length == 1 && ("-v".equals(arguments[0]) || "--version".equals(arguments[0]))) {
            parser.printVersion(output);
            return Optional.absent();
        }

        try {
            final Namespace namespace = parser.parseArgs(arguments);
            if (namespace.get("is-internal-action") != null) {
                return Optional.absent();
            }

            final Command.Description description = descriptions.get(namespace.getString(COMMAND_NAME_ATTR));
            return Optional.of(new Invocation(namespace, description.getCommandClass()));
        } catch (ArgumentParserException e) {
            output.println(e.getMessage());
            e.getParser().printHelp(output);
            return Optional.absent();
        }
    }

    private void addCommand(Command.Description command) {
        descriptions.put(command.getInfo().name(), command);
        parser.addSubparsers().help("available commands");
        final Subparser subparser = parser.addSubparsers().addParser(command.getInfo().name(), false);
        addHelp(subparser);
        params.get(command.getInfo().name()).configure(subparser);
        subparser.description(command.getInfo().description())
                 .setDefault(COMMAND_NAME_ATTR, command.getInfo().name());
    }

    private void addHelp(ArgumentParser p) {
        p.addArgument("-h", "--help")
                 .action(new HelpAction(output))
                 .help("show this help message and exit")
                 .setDefault(Arguments.SUPPRESS);
    }

    private ArgumentParser buildParser(JarLocation jarLocation) {
        final String usage = "java -jar " + jarLocation;
        final ArgumentParser p = ArgumentParsers.newArgumentParser(usage, false);
        addHelp(p);
        final Package pkg = jarLocation.getPackage();
        if (pkg == null || Strings.isNullOrEmpty(pkg.getImplementationVersion())) {
            p.version("No service version detected. Add a Implementation-Version " +
                              "entry to your JAR's manifest to enable this.");
        } else {
            p.version(pkg.getImplementationVersion());
        }
        p.addArgument("-v", "--version")
         .action(Arguments.help())
         .help("show the application version and exit");
        return p;
    }
}

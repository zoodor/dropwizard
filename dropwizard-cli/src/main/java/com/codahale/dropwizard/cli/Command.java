package com.codahale.dropwizard.cli;

import com.google.common.base.Optional;
import net.sourceforge.argparse4j.inf.Subparser;

import java.lang.annotation.*;

public interface Command {
    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {
        String name();

        String description();

        Class<? extends Params> params() default Params.None.class;
    }

    public interface Params {
        public static class None implements Params {
            @Override
            public void configure(Subparser subparser) {
            }
        }

        public static class ConfigFile implements Params {
            @Override
            public void configure(Subparser subparser) {
                subparser.addArgument("file").nargs("?").help("application configuration file");
            }
        }

        void configure(Subparser subparser);
    }

    public static class Description {
        private final Class<? extends Command> commandClass;
        private final Info info;

        private Description(Class<? extends Command> commandClass, Info info) {
            this.commandClass = commandClass;
            this.info = info;
        }

        public Class<? extends Command> getCommandClass() {
            return commandClass;
        }

        public Info getInfo() {
            return info;
        }

        public static Optional<Description> forClass(Class<? extends Command> klass) {
            final Info info = klass.getAnnotation(Info.class);
            if (info == null) {
                return Optional.absent();
            }
            return Optional.of(new Description(klass, info));
        }
    }

    public abstract void run() throws Exception;
}

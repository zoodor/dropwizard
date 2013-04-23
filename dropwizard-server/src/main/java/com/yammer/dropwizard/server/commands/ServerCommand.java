package com.yammer.dropwizard.server.commands;

import com.yammer.dropwizard.cli.Command;
import org.eclipse.jetty.server.Server;

import javax.inject.Inject;

@Command.Info(name = "server",
              description = "Runs the Dropwizard application as an HTTP server",
              params = Command.Params.ConfigFile.class)
public class ServerCommand implements Command {
    private final Server server;

    @Inject
    public ServerCommand(Server server) {
        this.server = server;
    }

    @Override
    public void run() throws Exception {
        try {
            server.start();
            server.join();
        } catch (Exception ignored) {
            server.stop();
            System.exit(-1);
        }
    }
}

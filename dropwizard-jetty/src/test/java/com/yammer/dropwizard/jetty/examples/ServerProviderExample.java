package com.yammer.dropwizard.jetty.examples;

import com.yammer.dropwizard.jetty.ServerConfiguration;
import com.yammer.dropwizard.jetty.ServerProvider;
import com.yammer.dropwizard.logging.Logging;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerProviderExample {
    public static void main(String[] args) throws Exception {
        Logging.bootstrap();

        final ServletContextHandler appHandler = new ServletContextHandler();
        final ServletHolder appHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                try (PrintWriter writer = resp.getWriter()) {
                    writer.println("app");
                }
            }
        });
        appHandler.addServlet(appHolder, "/*");

        final ServletContextHandler adminHandler = new ServletContextHandler();
        final ServletHolder adminHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                try (PrintWriter writer = resp.getWriter()) {
                    writer.println("admin");
                }
            }
        });
        adminHandler.addServlet(adminHolder, "/*");

        final ServerProvider provider = new ServerProvider(new ServerConfiguration(),
                                                           appHandler,
                                                           adminHandler,
                                                           "test");
        final Server server = provider.get();
        server.start();
        server.join();
    }
}

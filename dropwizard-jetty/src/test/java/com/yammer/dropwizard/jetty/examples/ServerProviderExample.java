package com.yammer.dropwizard.jetty.examples;

import com.yammer.dropwizard.jetty.ServerConfiguration;
import com.yammer.dropwizard.jetty.ServerProvider;
import com.yammer.dropwizard.logging.Logging;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
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
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                final PrintWriter writer = resp.getWriter();
                try {
                    writer.println("app");
                } finally {
                    writer.close();
                }
            }
        });
        appHandler.addServlet(appHolder, "/*");

        final ServletContextHandler adminHandler = new ServletContextHandler();
        final ServletHolder adminHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                final PrintWriter writer = resp.getWriter();
                try {
                    writer.println("admin");
                } finally {
                    writer.close();
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

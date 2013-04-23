package com.example.app;

import com.codahale.dropwizard.jackson.JacksonModule;
import com.codahale.dropwizard.jersey.JerseyModule;
import com.codahale.dropwizard.jetty.ServletModule;
import com.codahale.dropwizard.server.Application;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.name.Names;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MyApplication extends Application<MyConfiguration> {
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    public static class ExampleResource {
        @GET
        public List<String> poop() {
            return ImmutableList.of("yay", "woo");
        }
    }

    public static void main(String[] args) throws Exception {
        new MyApplication().run(args);
    }

    @Override
    public String getName() {
        return "my-application";
    }

    @Override
    protected void setup() {
        addModule(new JacksonModule() {
            @Override
            protected void configureJackson() {
                registerSubtype(String.class);
                bind(AfterburnerModule.class);
            }
        });

        addModule(new JerseyModule(Names.named("application"), "/*") {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
            }
        });

        addModule(new ServletModule(Names.named("admin")) {
            @Override
            protected void configureServlets() {
                serve("/*").with(new HttpServlet() {
                    @Override
                    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                        try (PrintWriter writer = resp.getWriter()) {
                            writer.println("admin!");
                        }
                    }
                });
            }
        });
    }
}

package com.codahale.dropwizard.jetty.benchmarks;

import com.codahale.dropwizard.jetty.RoutingHandler;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.google.common.collect.ImmutableMap;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;

@SuppressWarnings("EqualsAndHashcode")
public class RoutingHandlerBenchmark extends SimpleBenchmark {
    public static void main(String[] args) throws Exception {
        new Runner().run(
                RoutingHandlerBenchmark.class.getCanonicalName()
                ,"--trials", "8"
        );
    }

    // OK to mock these, since no methods are ever called on them
    // everything else has to be real classes, otherwise we're benchmarking Mockito
    private final Connector connector1 = mock(Connector.class);
    private final Connector connector2 = mock(Connector.class);

    private final Handler handler1 = new AbstractHandler() {
        private int i;

        @Override
        public int hashCode() {
            return i;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
            i++;
        }
    };
    private final Handler handler2 = new AbstractHandler() {
        private int i;

        @Override
        public int hashCode() {
            return i;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
            i++;
        }
    };

    private final ImmutableMap<Connector, Handler> handlers = ImmutableMap.of(
            connector1, handler1,
            connector2, handler2
    );

    private final Handler arrayHandler = new RoutingHandler(handlers);
//    private final Handler mapHandler = new RoutingMapHandler(handlers);

    private final HttpChannel<?> channel = new HttpChannel<>(connector1, new HttpConfiguration(), null, null, new HttpInput<Object>() {
        @Override
        protected int remaining(Object item) {
            return 0;
        }

        @Override
        protected int get(Object item, byte[] buffer, int offset, int length) {
            return 0;
        }

        @Override
        protected void onContentConsumed(Object item) {
        }
    });

    private static final String TARGET = "woo";
    private static final int LOOPS = 1000000;

    private final Request baseRequest = new Request(channel, null);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);

    @SuppressWarnings("UnusedDeclaration")
    public int timeArrayHandler(int reps) throws IOException, ServletException {
        for (int i = 0; i < reps; i++) {
            for (int j = 0; j < LOOPS; j++) {
                arrayHandler.handle(TARGET, baseRequest, request, response);
            }
        }
        return handler1.hashCode() + handler2.hashCode();
    }

//    @SuppressWarnings("UnusedDeclaration")
//    public int timeMapHandler(int reps) throws IOException, ServletException {
//        for (int i = 0; i < reps; i++) {
//            for (int j = 0; j < LOOPS; j++) {
//                mapHandler.handle(TARGET, baseRequest, request, response);
//            }
//        }
//        return handler1.hashCode() + handler2.hashCode();
//    }
}

package com.yammer.dropwizard.jetty;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.fest.assertions.api.Assertions.assertThat;

public class BiDiGzipHandlerTest {
    private final Handler handler = new AbstractHandler() {
        @Override
        public void handle(String target,
                           Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
            final ByteArrayOutputStream entity = new ByteArrayOutputStream();
            entity.write("HANDLED: ".getBytes(Charsets.UTF_8));
            ByteStreams.copy(request.getInputStream(), entity);

            try (OutputStream output = response.getOutputStream()) {
                output.write(entity.toByteArray());
            }
        }
    };

    private final BiDiGzipHandler gzip = new BiDiGzipHandler(handler);
    private final Server server = new Server();
    private final LocalConnector connector = new LocalConnector(server);
    private final HttpTester.Request request = HttpTester.newRequest();

    @Before
    public void setUp() throws Exception {
        // always encode everything w/ gzip
        gzip.setMinGzipSize(1);

        request.setHeader("Host", "example.com");
        request.setVersion(HttpVersion.HTTP_1_1);
        request.setMethod("GET");
        request.setURI("/hello");

        server.setHandler(gzip);
        server.addConnector(connector);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void passesThroughRequestsWithNoAcceptEncodingHeader() throws Exception {
        final HttpTester.Response response = execute();

        assertThat(response.getStatus())
                .isEqualTo(200);

        assertThat(response.get(HttpHeader.CONTENT_ENCODING))
                .isNull();



        assertThat(response.getContent())
                .isEqualTo("HANDLED: ");
    }

    @Test
    public void encodesResponseEntitiesForRequestsWithAnAcceptEncodingHeader() throws Exception {
        request.setHeader(HttpHeader.ACCEPT_ENCODING.asString(), "gzip");

        final HttpTester.Response response = execute();

        assertThat(response.getStatus())
                .isEqualTo(200);

        assertThat(response.get(HttpHeader.CONTENT_ENCODING))
                .isEqualTo("gzip");

        assertThat(decodeEntity(response))
                .isEqualTo("HANDLED: ");
    }

    @Test
    public void decodesRequestEntitiesWithAContentEncodingHeader() throws Exception {
        request.setMethod("POST");
        request.setHeader(HttpHeader.CONTENT_ENCODING.asString(), "gzip");
        request.setContent(encodeEntity("YAY"));

        final HttpTester.Response response = execute();

        assertThat(response.getStatus())
                .isEqualTo(200);

        assertThat(response.get(HttpHeader.CONTENT_ENCODING))
                .isNull();

        assertThat(response.getContent())
                .isEqualTo("HANDLED: YAY");
    }

    private HttpTester.Response execute() throws Exception {
        return HttpTester.parseResponse(connector.getResponses(request.generate(), 100, TimeUnit.MILLISECONDS));
    }

    private String decodeEntity(HttpTester.Response response) throws IOException {
        return new String(
                ByteStreams.toByteArray(
                        new GZIPInputStream(new ByteArrayInputStream(response.getContentBytes()))
                ),
                Charsets.UTF_8);
    }

    private ByteBuffer encodeEntity(String entity) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final GZIPOutputStream gzipOut = new GZIPOutputStream(output);
        gzipOut.write(entity.getBytes(Charsets.UTF_8));
        gzipOut.close();
        return ByteBuffer.wrap(output.toByteArray());
    }
}

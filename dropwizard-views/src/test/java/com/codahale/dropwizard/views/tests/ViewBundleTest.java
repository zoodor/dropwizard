package com.codahale.dropwizard.views.tests;

import com.codahale.dropwizard.config.Environment;
import com.codahale.dropwizard.setup.JerseyEnvironment;
import com.codahale.dropwizard.views.ViewBundle;
import com.codahale.dropwizard.views.ViewMessageBodyWriter;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ViewBundleTest {
    private final JerseyEnvironment jerseyEnvironment = mock(JerseyEnvironment.class);
    private final Environment environment = mock(Environment.class);

    @Before
    public void setUp() throws Exception {
        when(environment.getJerseyEnvironment()).thenReturn(jerseyEnvironment);
    }

    @Test
    public void addsTheViewMessageBodyWriterToTheEnvironment() throws Exception {
        new ViewBundle().run(environment);

        verify(jerseyEnvironment).addProvider(ViewMessageBodyWriter.class);
    }
}

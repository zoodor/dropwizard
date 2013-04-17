package com.yammer.dropwizard.jersey;

import com.google.inject.Guice;
import com.sun.jersey.api.core.ResourceConfig;
import com.yammer.dropwizard.jersey.resources.ExampleProvider;
import com.yammer.dropwizard.jersey.resources.ExampleResource;
import com.yammer.dropwizard.jersey.resources.LastResource;
import com.yammer.dropwizard.jersey.resources.OtherResource;
import com.yammer.dropwizard.jersey.validation.ConstraintViolationExceptionMapper;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.junit.After;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.fest.assertions.api.Assertions.assertThat;

public class JerseyModuleTest {
    static {
        Logger.getLogger("com.sun.jersey").setLevel(Level.OFF);
    }

    private ServletContextHandler handler;

    @After
    public void tearDown() throws Exception {
        handler.stop();
    }

    @Test
    public void bindsResourceClasses() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
            }
        });

        assertThat(container.getConfig().getClasses())
            .contains(ExampleResource.class);
    }

    @Test
    public void bindsProviderClasses() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
                bindProvider(ConstraintViolationExceptionMapper.class);
            }
        });

        assertThat(container.getConfig().getClasses())
                .contains(ConstraintViolationExceptionMapper.class);
    }

    @Test
    public void scansPackagesForClasses() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindEntirePackage(ExampleResource.class);
            }
        });

        assertThat(container.getConfig().getClasses())
                .contains(ExampleResource.class, LastResource.class, OtherResource.class,
                          ExampleProvider.class);
    }

    @Test
    public void enablesFeatures() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
                enableFeature(ResourceConfig.FEATURE_NORMALIZE_URI);
            }
        });

        assertThat(container.getConfig().getFeature(ResourceConfig.FEATURE_NORMALIZE_URI))
                .isTrue();
    }

    @Test
    public void disablesFeatures() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
                disableFeature(ResourceConfig.FEATURE_NORMALIZE_URI);
            }
        });

        assertThat(container.getConfig().getFeature(ResourceConfig.FEATURE_NORMALIZE_URI))
                .isFalse();
    }

    @Test
    public void setsProperties() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
                setProperty(ResourceConfig.COMMON_DELIMITERS, ";");
            }
        });

        assertThat(container.getConfig().getProperty(ResourceConfig.COMMON_DELIMITERS))
                .isEqualTo(";");
    }

    @Test
    public void bindsExtensionsToMediaTypes() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
                bindExtension("json").toMediaType(MediaType.APPLICATION_JSON_TYPE);
            }
        });

        assertThat(container.getConfig().getMediaTypeMappings().get("json"))
                .isEqualTo(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void bindsExtensionsToLocales() throws Exception {
        final GuiceContainer container = buildContainer(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
                bindExtension("english").toLanguage(Locale.ENGLISH);
            }
        });

        assertThat(container.getConfig().getLanguageMappings().get("english"))
                .isEqualTo("en");
    }

    @Test
    public void defaultsToBindingToRoot() throws Exception {
        this.handler = Guice.createInjector(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
            }
        }).getInstance(ServletContextHandler.class);

        final ServletMapping mapping = handler.getServletHandler().getServletMappings()[0];
        assertThat(mapping.getPathSpecs())
                .containsOnly("/*");
    }

    @Test
    public void bindsJerseyToAUrlSpec() throws Exception {
        this.handler = Guice.createInjector(new JerseyModule() {
            @Override
            protected void configureJersey() {
                bindResource(ExampleResource.class);
                setUrlSpec("/api/*");
            }
        }).getInstance(ServletContextHandler.class);

        final ServletMapping mapping = handler.getServletHandler().getServletMappings()[0];
        assertThat(mapping.getPathSpecs())
                .containsOnly("/api/*");
    }

    private GuiceContainer buildContainer(JerseyModule module) throws Exception {
        this.handler = Guice.createInjector(module).getInstance(ServletContextHandler.class);

        handler.start();

        final ServletHolder holder = handler.getServletHandler().getServlets()[0];
        final Servlet servlet = holder.getServletInstance();
        assertThat(servlet)
                .isInstanceOf(GuiceContainer.class);

        return (GuiceContainer) servlet;
    }
}

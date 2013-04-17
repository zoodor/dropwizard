package com.yammer.dropwizard.jersey;

import com.google.inject.Scopes;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import com.sun.jersey.spi.scanning.PathProviderScannerListener;
import com.yammer.dropwizard.jetty.ServletModule;

import javax.ws.rs.core.MediaType;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.sun.jersey.api.core.ResourceConfig.isProviderClass;
import static com.sun.jersey.api.core.ResourceConfig.isRootResourceClass;

public abstract class JerseyModule extends ServletModule {
    private final ResourceConfig config = new DefaultResourceConfig();
    private String urlSpec = "/*";

    @Override
    protected final void configureServlets() {
        configureJersey();
        bind(ResourceConfig.class).toInstance(config);
        serve(urlSpec).with(GuiceContainer.class).initOrder(1);
    }

    protected abstract void configureJersey();

    protected void setUrlSpec(String urlSpec) {
        this.urlSpec = urlSpec;
    }

    protected void bindResource(Class<?> klass) {
        checkArgument(isRootResourceClass(klass),
                      "%s is not a @Path-annotated class",
                      klass.getName());
        bind(klass).in(Scopes.SINGLETON);
    }

    protected void bindProvider(Class<?> klass) {
        checkArgument(isProviderClass(klass),
                      "%s is not a @Provider-annotated class",
                      klass.getName());
        bind(klass).in(Scopes.SINGLETON);
    }

    protected void bindEntirePackage(Class<?> klass, Class<?>... classes) {
        checkNotNull(classes);
        final String[] names = new String[classes.length+1];
        names[0] = klass.getPackage().getName();
        for (int i = 0; i < classes.length; i++) {
            names[i+1] = classes[i].getPackage().getName();
        }
        final PackageNamesScanner scanner = new PackageNamesScanner(names);
        final AnnotationScannerListener asl = new PathProviderScannerListener();
        scanner.scan(asl);
        for (Class<?> annotatedClass : asl.getAnnotatedClasses()) {
            if (isProviderClass(annotatedClass)) {
                bindProvider(annotatedClass);
            } else {
                bindResource(annotatedClass);
            }
        }
    }

    /**
     * Enables the Jersey feature with the given name.
     *
     * @param name the name of the feature to be enabled
     * @see com.sun.jersey.api.core.ResourceConfig
     */
    protected void enableFeature(String name) {
        config.getFeatures().put(checkNotNull(name), Boolean.TRUE);
    }

    /**
     * Disables the Jersey feature with the given name.
     *
     * @param name the name of the feature to be disabled
     * @see com.sun.jersey.api.core.ResourceConfig
     */
    protected void disableFeature(String name) {
        config.getFeatures().put(checkNotNull(name), Boolean.FALSE);
    }

    /**
     * Sets the given Jersey property.
     *
     * @param name  the name of the Jersey property
     * @param value the value of the Jersey property
     * @see com.sun.jersey.api.core.ResourceConfig
     */
    protected void setProperty(String name, Object value) {
        config.getProperties().put(checkNotNull(name), value);
    }

    /**
     * Gets the given Jersey property.
     *
     * @param name the name of the Jersey property
     * @see com.sun.jersey.api.core.ResourceConfig
     */
    @SuppressWarnings("unchecked")
    protected <T> T getProperty(String name) {
        return (T) config.getProperties().get(name);
    }

    protected ExtensionBinder bindExtension(String extension) {
        return new ExtensionBinder(config, extension);
    }

    public static class ExtensionBinder {
        private final ResourceConfig config;
        private final String extension;

        public ExtensionBinder(ResourceConfig config, String extension) {
            this.config = config;
            this.extension = extension;
        }

        protected ExtensionBinder toMediaType(MediaType type) {
            config.getMediaTypeMappings().put(extension, type);
            return this;
        }

        protected ExtensionBinder toLanguage(Locale locale) {
            config.getLanguageMappings().put(extension, locale.toString());
            return this;
        }
    }
}

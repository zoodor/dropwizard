package com.yammer.dropwizard.config;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An interface for objects that can create an {@link InputStream} to represent the service
 * configuration.
 */
public interface ConfigurationSource {
    class FileSource implements ConfigurationSource {
        private final String filename;

        @Inject
        public FileSource(String filename) {
            this.filename = filename;
        }

        @Override
        public InputStream get() throws IOException {
            return new FileInputStream(filename);
        }
    }

    class UrlSource implements ConfigurationSource {
        private final String filename;

        @Inject
        public UrlSource(String filename) {
            this.filename = filename;
        }

        @Override
        public InputStream get() throws IOException {
            return new FileInputStream(filename);
        }
    }

    /**
     * Returns an {@link java.io.InputStream} that contains the source of the configuration for the
     * service.
     *
     * @return a {@link java.io.InputStream}
     * @throws java.io.IOException if there is an error reading the data at {@code path}
     */
    public InputStream get() throws IOException;
}

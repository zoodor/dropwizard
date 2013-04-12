package com.yammer.dropwizard.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * An implementation of {@link SourceProvider} that reads the configuration from a
 * {@link URL}.
 */
public class UrlSourceProvider implements SourceProvider {
    @Override
    public InputStream create(String path) throws IOException {
        return new URL(path).openStream();
    }
}

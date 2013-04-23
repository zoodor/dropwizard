package com.codahale.dropwizard.config;

import java.io.*;

/**
 * An implementation of {@link SourceProvider} that reads the configuration from the
 * local file system.
 */
public class FileSourceProvider implements SourceProvider {
    @Override
    public InputStream create(String path) throws IOException {
        final File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file + " not found");
        }

        return new FileInputStream(file);
    }
}

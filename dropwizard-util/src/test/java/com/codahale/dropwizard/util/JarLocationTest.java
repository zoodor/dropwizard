package com.codahale.dropwizard.util;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class JarLocationTest {
    @Test
    public void isHumanReadable() throws Exception {
        assertThat(new JarLocation(JarLocationTest.class).toString())
                .isEqualTo("project.jar");
    }

    @Test
    public void hasAPackage() throws Exception {
        assertThat(new JarLocation(JarLocationTest.class).getPackage().getName())
                .isEqualTo("com.codahale.dropwizard.util");
    }
}

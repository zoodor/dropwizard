package com.codahale.dropwizard.testing.tests;

import com.codahale.dropwizard.testing.FixtureHelpers;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class FixtureHelpersTest {
    @Test
    public void readsTheFileAsAString() throws Exception {
        Assertions.assertThat(FixtureHelpers.fixture("fixtures/fixture.txt"))
                .isEqualTo("YAY FOR ME");
    }
}

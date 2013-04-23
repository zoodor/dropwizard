package com.codahale.dropwizard.testing.tests;

import com.codahale.dropwizard.testing.JsonHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

public class JsonHelpersTest {
    @Test
    public void readsJsonFixturesAsJsonNodes() throws Exception {
        final String json = "{\"name\":\"Coda\",\"email\":\"coda@example.com\"}";
        Assertions.assertThat(JsonHelpers.jsonFixture("fixtures/person.json"))
                .isEqualTo(json);
    }

    @Test
    public void convertsObjectsIntoJson() throws Exception {
        Assertions.assertThat(JsonHelpers.asJson(new Person("Coda", "coda@example.com")))
                .isEqualTo(JsonHelpers.jsonFixture("fixtures/person.json"));
    }

    @Test
    public void convertsJsonIntoObjects() throws Exception {
        Assertions.assertThat(JsonHelpers.fromJson(JsonHelpers.jsonFixture("fixtures/person.json"),
                                                   Person.class))
                .isEqualTo(new Person("Coda", "coda@example.com"));

        Assertions.assertThat(JsonHelpers.fromJson(JsonHelpers.jsonFixture("fixtures/person.json"),
                                                   new TypeReference<Person>() {
                                                   }))
                .isEqualTo(new Person("Coda", "coda@example.com"));
    }
}

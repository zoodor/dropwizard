package com.codahale.dropwizard.db.tests;

import com.codahale.dropwizard.db.DatabaseConfiguration;
import com.codahale.dropwizard.db.ManagedDataSource;
import com.codahale.dropwizard.db.ManagedDataSourceFactory;
import com.yammer.metrics.Metrics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.fest.assertions.api.Assertions.assertThat;

public class ManagedDataSourceFactoryTest {
    private final ManagedDataSourceFactory factory = new ManagedDataSourceFactory();

    private ManagedDataSource dataSource;

    @Before
    public void setUp() throws Exception {
        final DatabaseConfiguration config = new DatabaseConfiguration();
        config.setUrl("jdbc:hsqldb:mem:DbTest-" + System.currentTimeMillis());
        config.setUser("sa");
        config.setDriverClass("org.hsqldb.jdbcDriver");
        config.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");

        this.dataSource = factory.build(config);
    }

    @After
    public void tearDown() throws Exception {
        dataSource.stop();
        Metrics.defaultRegistry().shutdown();
    }

    @Test
    public void buildsAConnectionPoolToTheDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS");
            try {
                final ResultSet set = statement.executeQuery();
                try {
                    while (set.next()) {
                        assertThat(set.getInt(1)).isEqualTo(1);
                    }
                } finally {
                    set.close();
                }
            } finally {
                statement.close();
            }
        }
    }
}

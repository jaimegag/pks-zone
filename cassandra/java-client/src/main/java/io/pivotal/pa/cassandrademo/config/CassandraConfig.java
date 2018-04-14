package io.pivotal.pa.cassandrademo.config;

import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Created by jaguilar on 4/7/18.
 */
@Configuration
@EnableAutoConfiguration
@EnableCassandraRepositories(basePackages = { "io.pivotal.pa.cassandrademo.repo" })
public class CassandraConfig extends AbstractCassandraConfiguration {
    @Value("${vcap.services.cassandra-pks.credentials.hostname}")
    private String cassandraHost;

    @Value("${vcap.services.cassandra-pks.credentials.port:9042}")
    private int cassandraPort;

    public String getKeyspaceName() {
        return "pks";
    }

    public String getContactPoints() {
        return cassandraHost;

    }

    public int getPort() {
        return cassandraPort;
    }

    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Bean
    public CassandraTemplate cassandraTemplate(Session session) {
        return new CassandraTemplate(session);
    }
}

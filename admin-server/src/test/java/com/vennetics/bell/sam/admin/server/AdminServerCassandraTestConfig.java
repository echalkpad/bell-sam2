package com.vennetics.bell.sam.admin.server;

import com.vennetics.bell.sam.admin.server.utils.AdminServerTestUtils;
import com.vennetics.bell.sam.core.CoreErrorsConfig;
import com.vennetics.bell.sam.error.SamErrorsConfig;
import com.vennetics.bell.sam.rest.config.RestConfig;
import com.vennetics.microservices.common.api.model.admin.register.IServiceRegistration;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;

import static org.mockito.Mockito.mock;

/**
 * Cassandra config class
 */
@Configuration
@Import({ RestConfig.class, SamErrorsConfig.class, CoreErrorsConfig.class})
public class AdminServerCassandraTestConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return AdminServerTestUtils.KEYSPACE_NAME;
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(AdminServerTestUtils.CASSANDRA_HOST);
        cluster.setPort(EmbeddedCassandraServerHelper.getNativeTransportPort());
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
        return new BasicCassandraMappingContext();
    }

    /**
     * Loads the test YML file for testing the spring wiring.
     *
     * @return the property placeholder.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        final YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-admin-service-controller-test-config.yml"));
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public IServiceRegistration mockIServiceRegistration() {
        return mock(IServiceRegistration.class);
    }
}

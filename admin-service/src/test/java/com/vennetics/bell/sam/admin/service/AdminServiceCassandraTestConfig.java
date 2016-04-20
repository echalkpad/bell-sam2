package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.admin.service.api.impl.ApplicationAdminService;
import com.vennetics.bell.sam.admin.service.util.IAdminServiceTypeMapper;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper;
import com.vennetics.bell.sam.rest.config.converters.JsonConverter;
import com.vennetics.microservices.common.api.model.admin.register.IServiceRegistration;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import static org.mockito.Mockito.mock;

/**
 * Cassandra config class
 */
@Configuration
@EnableCassandraRepositories("com.vennetics.bell.sam.admin.service.repositories")
public class AdminServiceCassandraTestConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return TestUtils.KEYSPACE_NAME;
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(TestUtils.CASSANDRA_HOST);
        cluster.setPort(EmbeddedCassandraServerHelper.getNativeTransportPort());
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
        return new BasicCassandraMappingContext();
    }

    @Bean
    public ApplicationAdminService adminService() {
        return new ApplicationAdminService();
    }

    @Bean
    public JsonConverter jsonConverter() {
        return new JsonConverter();
    }

    @Bean
    public IAdminServiceTypeMapper adminServiceTypeMapper() {
        return mock(IAdminServiceTypeMapper.class);
    }

    @Bean
    public ITypeMapper typeMapper() {
        return mock(ITypeMapper.class);
    }

    @Bean
    public IServiceRegistration mockIServiceRegistration() {
        return mock(IServiceRegistration.class);
    }

}

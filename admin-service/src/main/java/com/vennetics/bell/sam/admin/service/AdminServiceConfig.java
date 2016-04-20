package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.admin.service.repositories.RoleRepository;
import com.vennetics.bell.sam.model.admin.service.config.AdminDataModelConfig;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

/**
 * The Admin AllowedService CassandraApplication
 */
@Configuration
@EnableCassandraRepositories
@Import(AdminDataModelConfig.class)
@ComponentScan("com.vennetics.bell.sam.admin.service")
public class AdminServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceConfig.class);

    @Autowired(required = true)
    private RoleRepository repository;

    /**
     * Initialize repo with an Admin user if one does not already exist.
     *
     * @return Jackson2RepositoryPopulatorFactoryBean
     */
    @Bean
    public Jackson2RepositoryPopulatorFactoryBean populateRepository() {

        final Jackson2RepositoryPopulatorFactoryBean factory =
                new Jackson2RepositoryPopulatorFactoryBean();

        factory.setResources(new Resource[] {});
        final CassandraRole cassandraRole = repository.findOne("admin");

        if (isDefaultUserRequired(cassandraRole)) {
            logger.info("Creating Default Admin User");
            final Resource sourceData = new ClassPathResource("setup.json");
            factory.setResources(new Resource[] { sourceData });
        }

        return factory;
    }


    /**
     * Method to determine if a default admin user is required
     * @param cassandraRole
     * @return boolean
     */
    public static boolean isDefaultUserRequired(final CassandraRole cassandraRole) {
        return null == cassandraRole || null == cassandraRole.getApplicationIds();
    }
}

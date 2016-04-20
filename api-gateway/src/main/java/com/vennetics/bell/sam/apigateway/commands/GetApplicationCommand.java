package com.vennetics.bell.sam.apigateway.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.vennetics.bell.sam.apigateway.security.ApplicationUserDetailsService;
import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixCommand;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * Command to call the Admin service to GET an application based on their id(ApplicationId)
 */
public class GetApplicationCommand extends AbstractHystrixCommand<CassandraApplication> {

    private static final Logger logger = LoggerFactory.getLogger(GetApplicationCommand.class);
    private final UUID applicationId;
    private final RestTemplate template;


    /**
     * @param applicationId     The applicationId for the CassandraApplication that is to be retrieved.
     * @param template          The {@link RestTemplate} to be used to address the service.
     */
    public GetApplicationCommand(final UUID applicationId, final RestTemplate template) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("AdminServerCommands"))
               .andCommandKey(HystrixCommandKey.Factory.asKey("GetApplication")));
        this.applicationId = applicationId;
        this.template = template;
    }

    @Override
    protected CassandraApplication run() {
        logger.debug("GetApplicationCommand about to GET CassandraApplication with Id {}", applicationId);
        CassandraApplication result = template.getForObject(buildUrl(CassandraApplication.TABLE_NAME, applicationId.toString()), CassandraApplication.class);
        logger.debug("GetApplicationCommand result {}", result);
        return result;
    }

    private static String buildUrl(final String tableName, final String name) {
        return UriComponentsBuilder.newInstance()
                                   .scheme(ApplicationUserDetailsService.HTTP_SCHEME)
                                   .host(ApplicationUserDetailsService.ADMIN_SERVER_NAME)
                                   .pathSegment(tableName, name)
                                   .toUriString();
    }
}

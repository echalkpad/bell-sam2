package com.vennetics.bell.sam.apigateway.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.vennetics.bell.sam.apigateway.security.ApplicationUserDetailsService;
import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixCommand;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Command to call the Admin service to GET a client based on their username(ClientId)
 */
public class GetClientCommand extends AbstractHystrixCommand<CassandraClient> {

    private static final Logger logger = LoggerFactory.getLogger(GetClientCommand.class);
    private final String username;
    private final RestTemplate template;

    /**
     * @param username         The username for the CassandraClient that is to be retrieved.
     * @param template          The {@link RestTemplate} to be used to address the service.
     */
    public GetClientCommand(final String username, final RestTemplate template) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("AdminServerCommands"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("GetClient")));
        this.username = username;
        this.template = template;
    }

    @Override
    protected CassandraClient run() {
        logger.debug("GetClientCommand about to GET CassandraClient with Id {}", username);
        CassandraClient result = template.getForObject(buildUrl(CassandraClient.TABLE_NAME, username), CassandraClient.class);
        logger.debug("GetClientCommand result {}", result);
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

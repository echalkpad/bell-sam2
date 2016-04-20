package com.vennetics.bell.sam.apigateway.server.commands;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vennetics.microservices.common.api.model.admin.AdminApiModelConstants;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.core.gateway.adapter.command.AbstractHystrixGatewayCommand;

/**
 * Queries admin-server for user details.
 */
public class GetUserDetailsCommand extends
                                   AbstractHystrixGatewayCommand<ResponseEntity<ApplicationUserDetails>> {

    private static final Logger logger = LoggerFactory.getLogger(GetUserDetailsCommand.class);

    private static final int DEFAULT_CACHE_MAX_SIZE = 1000;

    private static final int DEFAULT_CACHE_TIMEOUT = 30;

    private final String clientId;

    private final RestTemplate template;

    // Hard-coded Cache. In future we will want to replace this using HazelCast
    // For now keeping it to 30 seconds max so will factor in under load, but
    // not during infrequent access.
    public static final Cache<CharSequence, ApplicationUserDetails> CACHE = CacheBuilder.newBuilder()
                                                                                        .maximumSize(DEFAULT_CACHE_MAX_SIZE)
                                                                                        .expireAfterWrite(DEFAULT_CACHE_TIMEOUT,
                                                                                                          TimeUnit.SECONDS)
                                                                                        .build();

    /**
     * @param applicationId
     *            The applicationId for the CassandraApplication that is to be
     *            retrieved.
     * @param template
     *            The {@link RestTemplate} to be used to address the service.
     */
    public GetUserDetailsCommand(final String clientId, final RestTemplate template) {

        super("ApiGatewaySecurity", "GetUserDetailsCommand");

        this.clientId = clientId;
        this.template = template;
    }


    @Override
    protected ResponseEntity<ApplicationUserDetails> runCommand() {

        final ApplicationUserDetails details = CACHE.getIfPresent(clientId);
        if (details != null) {
            logger.debug("Found cached entity for clientId: {}", clientId);
            return new ResponseEntity<ApplicationUserDetails>(details, HttpStatus.OK);
        }

        // Do real remote lookup
        final String url = buildUrl();
        logger.debug("GetUserDetailsCommand clientId:{} using url: ", clientId, url);

        final ResponseEntity<ApplicationUserDetails> result = template.getForEntity(url,
                                                                                    ApplicationUserDetails.class);

        final ApplicationUserDetails foundDetails = result.getBody();

        logger.debug("GetUserDetailsCommand result {}", foundDetails);

        if (foundDetails != null) {
            logger.debug("Caching entity for clientId: {}", clientId);
            CACHE.put(clientId, foundDetails);
        }

        return result;
    }

    private String buildUrl() {

        return UriComponentsBuilder.newInstance()
                                   .scheme("http")
                                   .host("admin-server")
                                   .pathSegment(AdminApiModelConstants.PATH_PART_V1,
                                                AdminApiModelConstants.PATH_PART_USERDETAILS,
                                                clientId)
                                   .toUriString();
    }
}

package com.vennetics.bell.sam.apigateway.security;

import com.vennetics.bell.sam.apigateway.commands.GetApplicationCommand;
import com.vennetics.bell.sam.apigateway.commands.GetClientCommand;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

/**
 * Implementation for Authorizing Third Party Applications via the Admin Services' CRUD Repos
 */
@Service("applicationUserDetailsService")
public class ApplicationUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUserDetailsService.class);
    public static final String ADMIN_SERVER_NAME = "admin-server";
    public static final String HTTP_SCHEME = "http";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(final String username) {

        return new GetClientCommand(username, restTemplate)
                // Async execution
                .observe()
                .filter(client -> client != null)
                // Pass the result to the next command via FlatMap
                .flatMap(client -> new GetApplicationCommand(client.getApplicationId(), restTemplate)
                        .observe())
                        // Filter not-null
                        .filter(application -> application != null)
                        // Map to ApplicationUserDetails
                        .map(application -> {

                            final ArrayList<String> roles = new ArrayList<>();
                            roles.add(CassandraRole.SPRING_SEC_USER);

                            if (application.getRoleIds().contains(CassandraRole.ADMIN.toLowerCase())) {
                                roles.add(CassandraRole.SPRING_SEC_ADMIN);
                            }
                            logger.debug("Returning ApplicationUserDetails with roles {}", roles);
                            return new ApplicationUserDetails(application, roles);
                        })
                    .toBlocking()
                    .firstOrDefault(null);

    }
}

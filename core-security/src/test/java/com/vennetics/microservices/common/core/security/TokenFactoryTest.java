package com.vennetics.microservices.common.core.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.vennetics.microservices.common.api.model.admin.util.ApplicationUserDetailsBuilder;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

public class TokenFactoryTest {

    private static final String ROLE_ADMIN = "admin";

    private static final String ROLE_USER = "user";

    private static final String STATE_ACTIVE = "ACTIVE";

    private static final String ENCODED_SECRET = "encodedSecret";

    private static final String CLIENT_ID = "clientId";

    @Test
    public void shouldSupportTokenWithNoRolesAndAllowedServices() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_ACTIVE).build();

        final Authentication result = TokenFactory.createToken(userDetails);

        assertThat(result, not(nullValue()));
        assertThat(result.getPrincipal(), equalTo(CLIENT_ID));
        assertThat(result.getCredentials(), equalTo(ENCODED_SECRET));
        assertThat(result.getDetails(), equalTo(userDetails));
        assertThat(result.getAuthorities(), empty());
    }

    @Test
    public void shouldMapRolesToAuthorities() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_ACTIVE).withRoles(ROLE_USER,
                                                                                                             ROLE_ADMIN)
                                                                                                  .build();

        final Authentication result = TokenFactory.createToken(userDetails);

        // Upper case with ROLE prefix
        assertThat(result.getAuthorities(),
                   containsInAnyOrder(new SimpleGrantedAuthority("ROLE_USER"),
                                      new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void shouldMapServicesToAuthorities() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_ACTIVE).withServices(new ApplicationAllowedService("subscriber-thirdparty-sdm-server",
                                                                                                                                              null))
                                                                                                  .build();

        final Authentication result = TokenFactory.createToken(userDetails);

        assertThat(result.getAuthorities(),
                   contains(new SimpleGrantedAuthority("SERVICE_subscriber-thirdparty-sdm-server")));
    }


}

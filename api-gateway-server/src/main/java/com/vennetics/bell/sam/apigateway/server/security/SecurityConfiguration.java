package com.vennetics.bell.sam.apigateway.server.security;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.registry.ServiceConstants;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String ROLE_USER = "ROLE_USER";

    public static final String STANDARD_AUTH = "hasRole('%s') and isFullyAuthenticated()";

    @Autowired
    private RestTemplate gatewayAdapterRestTemplate;

    @Autowired
    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "squid:S00112" })
    public void configureAuth(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Autowired
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ICachedPasswordChecker passwordChecker() {
        return new CachedPasswordChecker(passwordEncoder());
    }

    @Override
    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "squid:S00112" })
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/api/**").access(format(STANDARD_AUTH, ROLE_USER))
        .antMatchers(ServiceConstants.BELL_SUBSCRIBER_URL + "/**").access(format(STANDARD_AUTH, ROLE_USER))
        .antMatchers(ServiceConstants.NETAPI_SMS_URL + "/**").access(format(STANDARD_AUTH, ROLE_USER))
        .antMatchers("/hystrix.stream").permitAll()
        .antMatchers("/health").permitAll()
        .antMatchers("/**").access(format(STANDARD_AUTH, "ROLE_ADMIN"))
        .and().httpBasic();
        // @formatter:on
    }

    @Autowired
    public AuthenticationProvider authProvider() {
        return new ApplicationAuthenticationProvider(passwordChecker(), gatewayAdapterRestTemplate);
    }

}

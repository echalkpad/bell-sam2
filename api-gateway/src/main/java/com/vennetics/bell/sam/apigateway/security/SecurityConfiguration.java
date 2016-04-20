package com.vennetics.bell.sam.apigateway.security;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import com.vennetics.bell.sam.registry.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.lang.String.format;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String STANDARD_AUTH = "hasRole('%s') and isFullyAuthenticated()";
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "squid:S00112" })
    public void configureAuth(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Autowired
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "squid:S00112" })
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/**").access(format(STANDARD_AUTH, CassandraRole.USER))
                .antMatchers(ServiceConstants.BELL_SUBSCRIBER_URL + "/**").access(format(STANDARD_AUTH, CassandraRole.USER))
                .antMatchers(ServiceConstants.NETAPI_SMS_URL + "/**").access(format(STANDARD_AUTH, CassandraRole.USER))
                .antMatchers("/hystrix.stream").permitAll()
                .antMatchers("/health").permitAll()
                .antMatchers("/**").access(format(STANDARD_AUTH, CassandraRole.ADMIN))
            .and().httpBasic();
        // @formatter:on
    }

    @Autowired
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}


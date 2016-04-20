package com.vennetics.bell.sam.admin.service.userdetails;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.vennetics.bell.sam.admin.service.repositories.ApplicationRepository;
import com.vennetics.bell.sam.admin.service.repositories.ClientRepository;
import com.vennetics.bell.sam.admin.service.util.IAdminServiceTypeMapper;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import com.vennetics.bell.sam.model.admin.service.pojo.TypeMapper;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.APPLICATION_POLICY1;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.APP_POLICY_SET;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY1;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY_IDS;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY_SET;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminApplicationUserDetailsServiceTest {

    private static final UUID APP_ID = UUID.randomUUID();

    private static final String CLIENT_ID = "clientId";

    private static final CassandraApplication CASSANDRA_APPLICATION = new CassandraApplication(APP_ID,
                                                                                               CLIENT_ID,
                                                                                               "name",
                                                                                               "email",
                                                                                               "secret",
                                                                                               "ACTIVE",
                                                                                               null,
                                                                                               null);

    private static final TypeMapper TYPE_MAPPER = new TypeMapper();

    static {
        CASSANDRA_APPLICATION.setRoleIds(ROLES);
        CASSANDRA_APPLICATION.setAllowedServiceIds(SERVICE_POLICY_IDS);
        CASSANDRA_APPLICATION.setGatewayPolicies(TYPE_MAPPER.toPoliciesAsString(SERVICE_POLICY_SET));
        CASSANDRA_APPLICATION.setPolicies(TYPE_MAPPER.toPoliciesAsString(APP_POLICY_SET));
    }

    private static final CassandraClient CASSANDRA_CLIENT = new CassandraClient(CLIENT_ID, APP_ID);

    private static final Set<ApplicationAllowedService> ALLOWED_SERVICES =
        new HashSet<>(Arrays.asList(new ApplicationAllowedService(SERVICE_POLICY1.getServiceTypeId(),
                                                                  new HashSet<>(Arrays.asList(new ApplicationPolicy(SERVICE_POLICY1.getPolicyId(),
                                                                                                                    SERVICE_POLICY1.getValues()))))));

    private static final Set<ApplicationPolicy> APPLICATION_POLICIES =
        new HashSet<>(Arrays.asList(new ApplicationPolicy(APPLICATION_POLICY1.getPolicyId(),
                                                          APPLICATION_POLICY1.getValues())));
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private IAdminServiceTypeMapper typeMapper;

    private AdminApplicationUserDetailsService testClass;

    private HystrixRequestContext hystrixRequestContext;

    @Before
    public void init() {

        hystrixRequestContext = HystrixRequestContext.initializeContext();
        testClass = new AdminApplicationUserDetailsService(applicationRepository,
                                                           clientRepository,
                                                           typeMapper);
    }

    @After
    public void destroy() {
        hystrixRequestContext.shutdown();
    }

    @Test
    public void shouldReturnMappedAttributesOnSuccess() {

        when(clientRepository.findOne(CLIENT_ID)).thenReturn(CASSANDRA_CLIENT);
        when(applicationRepository.findOne(APP_ID)).thenReturn(CASSANDRA_APPLICATION);

        when(typeMapper.toServicePolicies(CASSANDRA_APPLICATION)).thenReturn(SERVICE_POLICY_SET);
        when(typeMapper.toApplicationPolicies(CASSANDRA_APPLICATION)).thenReturn(APP_POLICY_SET);
        when(typeMapper.toAllowedServices(SERVICE_POLICY_IDS,
                                          SERVICE_POLICY_SET)).thenReturn(ALLOWED_SERVICES);
        when(typeMapper.toApplicationPolicies(APP_POLICY_SET)).thenReturn(APPLICATION_POLICIES);

        final ApplicationUserDetails result = testClass.loadByClientId(CLIENT_ID)
                                                       .toBlocking()
                                                       .single();

        assertThat(result.getClientId(), equalTo(CLIENT_ID));
        assertThat(result.getSecret(), equalTo("secret"));
        assertThat(result.getState(), equalTo("ACTIVE"));
        assertThat(result.getAllowedServices(), equalTo(ALLOWED_SERVICES));
        assertThat(result.getApplicationPolicies(), equalTo(APPLICATION_POLICIES));

    }

    @Test
    public void shouldAllowNotFoundInClientRepo() {

        when(clientRepository.findOne(CLIENT_ID)).thenReturn(null);
        when(applicationRepository.findOne(APP_ID)).thenReturn(CASSANDRA_APPLICATION);

        final ApplicationUserDetails result = testClass.loadByClientId(CLIENT_ID)
                                                       .toBlocking()
                                                       .single();

        assertNull(result);

    }

    @Test
    public void shouldAllowNotFoundInApplicationRepo() {

        when(clientRepository.findOne(CLIENT_ID)).thenReturn(CASSANDRA_CLIENT);
        when(applicationRepository.findOne(APP_ID)).thenReturn(null);

        final ApplicationUserDetails result = testClass.loadByClientId(CLIENT_ID)
                                                       .toBlocking()
                                                       .single();

        assertNull(result);

    }

    @Test
    public void shouldPropogateThrownException() {

        when(clientRepository.findOne(CLIENT_ID)).thenReturn(CASSANDRA_CLIENT);
        when(applicationRepository.findOne(APP_ID)).thenThrow(new PlatformFailedException(""));

        try {
            testClass.loadByClientId(CLIENT_ID).toBlocking().single();

            fail("Expected runtime exception");
        } catch (final HystrixRuntimeException e) {
            assertThat(e.getCause(), instanceOf(PlatformFailedException.class));
        }

    }

}

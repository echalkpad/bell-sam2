package com.vennetics.bell.sam.admin.service.util;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper;
import com.vennetics.bell.sam.model.admin.service.pojo.Policy;
import com.vennetics.bell.sam.model.admin.service.pojo.TypeMapper;
import com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.vennetics.bell.sam.admin.service.utils.TestUtils.DESCRIPTION;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_ID;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_META_DATAS;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.APPLICATION_POLICY1;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.APPLICATION_POLICY2;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.APP_POLICY_SET;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY1;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY2;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY_ID1;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY_ID2;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY_SET;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_TYPE_IDS;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
public class AdminServiceTypeMapperTest {

    private ITypeMapper typeMapper;

    private AdminServiceTypeMapper testClass;

    @Before
    public void init() {
        typeMapper = new TypeMapper();
        testClass = new AdminServiceTypeMapper(typeMapper);
    }

    @Test
    public void shouldMapSetPoliciesToApplicationPolicies() {

        final CassandraApplication cassandraApplication = new CassandraApplication();

        // Null safe checks
        assertThat(testClass.toServicePolicies(cassandraApplication), empty());
        assertThat(testClass.toApplicationPolicies(cassandraApplication), empty());

        cassandraApplication.setPolicies(typeMapper.toPoliciesAsString(SERVICE_POLICY_SET));
        cassandraApplication.setGatewayPolicies(typeMapper.toPoliciesAsString(APP_POLICY_SET));

        assertThat(testClass.toServicePolicies(cassandraApplication), equalTo(SERVICE_POLICY_SET));
        assertThat(testClass.toApplicationPolicies(cassandraApplication), equalTo(APP_POLICY_SET));
    }

    @Test
    public void shouldMapOnlyServicePoliciesToAllowedServices() {

        final CassandraApplication cassandraApplication = new CassandraApplication();
        cassandraApplication.setPolicies(typeMapper.toPoliciesAsString(SERVICE_POLICY_SET));
        cassandraApplication.setGatewayPolicies(typeMapper.toPoliciesAsString(APP_POLICY_SET));
        cassandraApplication.setAllowedServiceIds(SERVICE_TYPE_IDS);

        final Set<Policy> policies = testClass.toServicePolicies(cassandraApplication);

        final Set<ApplicationAllowedService> allowedServices = testClass.toAllowedServices(SERVICE_TYPE_IDS,
                                                                                           policies);

        assertThat(allowedServices,
                   containsInAnyOrder(new ApplicationAllowedService(SERVICE_POLICY1.getServiceTypeId(),
                                                                    new HashSet<>(Arrays.asList(new ApplicationPolicy(SERVICE_POLICY_ID1,
                                                                                                                      SERVICE_POLICY1.getValues())))),
                                      new ApplicationAllowedService(SERVICE_POLICY2.getServiceTypeId(),
                                                                    new HashSet<>(Arrays.asList(new ApplicationPolicy(SERVICE_POLICY_ID2,
                                                                                                                      SERVICE_POLICY2.getValues()))))));

        assertThat(allowedServices,
                   not(contains(new ApplicationAllowedService(APPLICATION_POLICY1.getServiceTypeId(),
                                                              new HashSet<>(Arrays.asList(new ApplicationPolicy(APPLICATION_POLICY1.getPolicyId(),
                                                                                                                APPLICATION_POLICY1.getValues())))))));
        assertThat(allowedServices,
                   not(contains(new ApplicationAllowedService(APPLICATION_POLICY2.getServiceTypeId(),
                                                              new HashSet<>(Arrays.asList(new ApplicationPolicy(APPLICATION_POLICY2.getPolicyId(),
                                                                                                                APPLICATION_POLICY2.getValues())))))));
    }

    @Test
    public void shouldMapServicesWithNoPolicies() {

        final CassandraApplication cassandraApplication = new CassandraApplication();
        cassandraApplication.setAllowedServiceIds(SERVICE_TYPE_IDS);

        final Set<Policy> policies = testClass.toServicePolicies(cassandraApplication);

        final Set<ApplicationAllowedService> allowedServices = testClass.toAllowedServices(AdminDataModelTestData.SERVICE_TYPE_IDS,
                                                                                           policies);

        assertThat(allowedServices,
                   containsInAnyOrder(new ApplicationAllowedService(SERVICE_POLICY1.getServiceTypeId(),
                                                                    null),
                                      new ApplicationAllowedService(SERVICE_POLICY2.getServiceTypeId(),
                                                                    null)));

        assertThat(allowedServices,
                   not(contains(new ApplicationAllowedService(APPLICATION_POLICY1.getServiceTypeId(),
                                                              new HashSet<>(Arrays.asList(new ApplicationPolicy(APPLICATION_POLICY1.getPolicyId(),
                                                                                                                APPLICATION_POLICY1.getValues())))))));
    }

    @Test
    public void shouldMapOnlyNonServicePoliciesToAppPolicies() {

        final CassandraApplication cassandraApplication = new CassandraApplication();
        cassandraApplication.setPolicies(typeMapper.toPoliciesAsString(SERVICE_POLICY_SET));
        cassandraApplication.setGatewayPolicies(typeMapper.toPoliciesAsString(APP_POLICY_SET));
        cassandraApplication.setAllowedServiceIds(SERVICE_TYPE_IDS);

        final Set<Policy> policies = testClass.toApplicationPolicies(cassandraApplication);

        final Set<ApplicationPolicy> applicationPolicies = testClass.toApplicationPolicies(policies);

        assertThat(applicationPolicies,
                   containsInAnyOrder(new ApplicationPolicy(APPLICATION_POLICY1.getPolicyId(),
                                                            APPLICATION_POLICY1.getValues()),
                                      new ApplicationPolicy(APPLICATION_POLICY2.getPolicyId(),
                                                            APPLICATION_POLICY2.getValues())));

        assertThat(applicationPolicies,
                   not(contains(new ApplicationPolicy(SERVICE_POLICY1.getPolicyId(),
                                                      SERVICE_POLICY1.getValues()))));
    }


    @Test
    public void shouldMapToPolicyTypeList() {

        final PolicyTypeList policyTypeList = testClass.toPolicyTypeList(POLICY_META_DATAS);
        Integer order = 0;
        assertEquals(policyTypeList.getPolicyTypes().get(0).getDescription(), DESCRIPTION);
        assertEquals(policyTypeList.getPolicyTypes().get(0).getPolicyOrder(), order);
        assertEquals(policyTypeList.getPolicyTypes().get(0).getPolicyTypeId(), POLICY_ID);
        assertNull(policyTypeList.getPolicyTypes().get(0).getServiceTypeIds());
    }
}

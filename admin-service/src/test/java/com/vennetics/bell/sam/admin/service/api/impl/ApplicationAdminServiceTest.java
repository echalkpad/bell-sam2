package com.vennetics.bell.sam.admin.service.api.impl;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceEntityNotFoundException;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceInvalidPolicyKeysException;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceNoTypesException;
import com.vennetics.bell.sam.admin.service.util.IAdminServiceTypeMapper;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraAllowedService;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraProtocolType;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper;
import com.vennetics.bell.sam.model.admin.service.pojo.Policy;
import com.vennetics.microservices.common.api.model.admin.register.IServiceRegistration;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetailsList;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.Role;
import com.vennetics.microservices.common.api.model.admin.v1.application.RoleList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceTypeList;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import rx.Observable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.vennetics.bell.sam.admin.service.utils.TestUtils.ACCOUNT_STATE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.ALLOWED_SERVICES;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_ID_1;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_ID_2;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_POLICIES;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_POLICY;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_POLICY_2;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CLIENT_ID_1;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CLIENT_ID_2;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.EMAIL;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.FULL_APPLICATION_USER_DETAILS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.KEY;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.NAME;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICIES_STRING;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_ID;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_ID_2;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_META_DATA;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_META_DATAS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_TYPES;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.ROLE_ADMIN;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SECRET;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_ID;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_IDS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_POLICIES_STRING;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_TYPE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_TYPES;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.UPDATED_NAME;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test class for ApplicationAdminService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableCassandraRepositories
@ContextConfiguration(classes = {AdminServiceCassandraTestConfig.class})
@ComponentScan("com.vennetics.bell.sam.admin.service")
@Profile("dev")
@WebAppConfiguration
public class ApplicationAdminServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAdminServiceTest.class);

    @Autowired
    private ApplicationAdminService adminService;
    @Autowired
    private CassandraAdminTemplate cassandraTemplate;
    @Autowired
    private IServiceRegistration mockIServiceRegistration;
    @Autowired
    private IAdminServiceTypeMapper adminServiceTypeMapper;
    @Autowired
    private ITypeMapper typeMapper;


    @Value("${local.server.port}")
    private static int port;

    private CassandraApplication cassandraApplication1;
    private CassandraApplication cassandraApplication2;
    private CassandraClient cassandraClient1;
    private Set<Policy> policySet;

    @BeforeClass
    public static void setUp() throws Exception {
        EmbeddedCassandraTestHelper.startCassandraCluster(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE,
                                                          port,
                                                          TestUtils.CASSANDRA_HOST,
                                                          TestUtils.KEYSPACE_CREATE,
                                                          TestUtils.CREATE_ALLOWED_SERVICES_TABLE,
                                                          TestUtils.CREATE_APPLICATION_TABLE,
                                                          TestUtils.CREATE_CLIENT_TABLE,
                                                          TestUtils.CREATE_POLICY_TYPE_TABLE,
                                                          TestUtils.CREATE_PROTOCOL_TYPE_TABLE,
                                                          TestUtils.CREATE_ROLE_TABLE,
                                                          TestUtils.CREATE_SERVICE_TYPE_TABLE);
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Before
    public void setupTestVariables() {
        cassandraApplication1 = TestUtils.createDummyCassApplication();
        cassandraApplication2 = TestUtils.createDummyCassApplication2();
        cassandraClient1 = TestUtils.createDummyCassClient();
        policySet = new HashSet<>();
        policySet.add(POLICY);
    }

    @Test
    public void testCreateApplication() throws Exception {

        // Success Test
        final ApplicationUserDetails applicationToCreate = new ApplicationUserDetails(CLIENT_ID_1,
                                                                                      NAME,
                                                                                      EMAIL,
                                                                                      SECRET,
                                                                                      ACCOUNT_STATE,
                                                                                      null);

        Observable<ApplicationUserDetails> appUserDetailsObservable = adminService.createApplication(applicationToCreate);
        assertNotNull(appUserDetailsObservable);

        final ApplicationUserDetails singleApplication = appUserDetailsObservable.toBlocking().single();

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
                QueryBuilder.eq(CassandraApplication.APPLICATION_ID, singleApplication.getApplicationId())).limit(10);

        assertEquals(applicationToCreate.getClientId(), singleApplication.getClientId());
        assertEquals(applicationToCreate.getName(), singleApplication.getName());
        assertEquals(applicationToCreate.getEmail(), singleApplication.getEmail());
        assertEquals(applicationToCreate.getState(), singleApplication.getState());
        assertNotNull(singleApplication.getApplicationId());
        assertNull(singleApplication.getRoles());
        assertNull(singleApplication.getApplicationPolicies());
        assertNull(singleApplication.getAllowedServices());

        final CassandraApplication cassandraApplicationResult = cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class);
        assertEquals(applicationToCreate.getClientId(), cassandraApplicationResult.getClientId());
        assertEquals(applicationToCreate.getName(), cassandraApplicationResult.getName());
        assertEquals(applicationToCreate.getEmail(), cassandraApplicationResult.getEmail());
        assertEquals(applicationToCreate.getState(), cassandraApplicationResult.getAccountState());
        assertNotNull(cassandraApplicationResult.getSecret());
        assertNotNull(cassandraApplicationResult.getCreatedTimestamp());
        assertNotNull(cassandraApplicationResult.getLastModifiedTimestamp());

        final Select clientSelect = QueryBuilder.select().from(CassandraClient.TABLE_NAME).where(
                QueryBuilder.eq(CassandraClient.CLIENT_ID, applicationToCreate.getClientId())).limit(10);

        final CassandraClient cassandraClientResult = cassandraTemplate.selectOne(clientSelect, CassandraClient.class);
        assertEquals(applicationToCreate.getClientId(), cassandraClientResult.getClientId());
        assertEquals(singleApplication.getApplicationId(), cassandraClientResult.getApplicationId());
        assertNotNull(cassandraClientResult.getCreatedTimestamp());
        assertNotNull(cassandraClientResult.getLastModifiedTimestamp());
    }

    @Test
    public void testGetAllApplications() throws Exception {
        cassandraTemplate.insert(cassandraApplication1);
        cassandraTemplate.insert(cassandraApplication2);
        final Observable<ApplicationUserDetailsList> applications = adminService.getAllApplications();
        assertEquals(2, applications.toBlocking().single().getApplications().size());
        cassandraTemplate.delete(cassandraApplication1);
        cassandraTemplate.delete(cassandraApplication2);
    }


    @Test
    public void testGetApplicationById() throws Exception {
        Observable<ApplicationUserDetails> application;
        UUID unknownId = UUIDs.timeBased();
        try {
            application = adminService.getApplicationById(unknownId);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(unknownId.toString()));
        }

        cassandraTemplate.insert(cassandraApplication1);

        when(adminServiceTypeMapper.toApplicationPolicies(
            adminServiceTypeMapper.toApplicationPolicies(cassandraApplication1)))
                                   .thenReturn(APPLICATION_POLICIES);

        when(adminServiceTypeMapper.toAllowedServices(
            cassandraApplication1.getAllowedServiceIds(),
            adminServiceTypeMapper.toServicePolicies(cassandraApplication1)))
                                   .thenReturn(ALLOWED_SERVICES);

        application = adminService.getApplicationById(FULL_APPLICATION_USER_DETAILS.getApplicationId());
        assertNotNull(application.toBlocking().single());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getApplicationId(), application.toBlocking().single().getApplicationId());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getClientId(), application.toBlocking().single().getClientId());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getName(), application.toBlocking().single().getName());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getEmail(), application.toBlocking().single().getEmail());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getState(), application.toBlocking().single().getState());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getRoles(), application.toBlocking().single().getRoles());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getApplicationPolicies(), application.toBlocking().single().getApplicationPolicies());
        assertEquals(FULL_APPLICATION_USER_DETAILS.getAllowedServices(), application.toBlocking().single().getAllowedServices());
        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testDeleteApplication() throws Exception {

        try {
            adminService.deleteApplication(APPLICATION_ID_2);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(APPLICATION_ID_2.toString()));
        }

        cassandraTemplate.insert(cassandraApplication1);
        cassandraTemplate.insert(cassandraClient1);
        CassandraAllowedService cassandraAllowedService = TestUtils.createDummyCassAllowedService();
        CassandraRole cassandraRole = TestUtils.createDummyCassRole();
        cassandraTemplate.insert(cassandraRole);
        cassandraTemplate.insert(cassandraAllowedService);
        adminService.addService(cassandraApplication1.getApplicationId(), cassandraAllowedService.getAllowedServiceId());
        adminService.addRole(cassandraApplication1.getApplicationId(), cassandraRole.getRoleId());

        adminService.deleteApplication(cassandraApplication1.getApplicationId());

        final Select serviceSelect = QueryBuilder.select().from(CassandraAllowedService.TABLE_NAME).where(
                QueryBuilder.eq(CassandraAllowedService.ALLOWED_SERVICE_ID, cassandraAllowedService.getAllowedServiceId())).limit(10);
        assertThat(cassandraApplication1.getApplicationId(), not(cassandraTemplate.selectOne(serviceSelect, CassandraAllowedService.class).getApplicationIds()));

        final Select roleSelect = QueryBuilder.select().from(CassandraRole.TABLE_NAME).where(
                QueryBuilder.eq(CassandraRole.ROLE_ID, cassandraRole.getRoleId())).limit(10);
        assertThat(cassandraApplication1.getApplicationId(), not(cassandraTemplate.selectOne(roleSelect, CassandraRole.class).getApplicationIds()));

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
                QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication1.getApplicationId())).limit(10);
        assertNull(cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class));

        final Select clientSelect = QueryBuilder.select().from(CassandraClient.TABLE_NAME).where(
                QueryBuilder.eq(CassandraClient.CLIENT_ID, cassandraApplication1.getClientId())).limit(10);
        assertNull(cassandraTemplate.selectOne(clientSelect, CassandraClient.class));

        cassandraTemplate.delete(cassandraRole);
        cassandraTemplate.delete(cassandraAllowedService);
    }

    @Test
    public void testUpdateApplication() throws Exception {

        cassandraTemplate.insert(cassandraApplication1);
        final ApplicationUserDetails application = new ApplicationUserDetails(CLIENT_ID_2,
                                                                              UPDATED_NAME,
                                                                              EMAIL,
                                                                              SECRET,
                                                                              ACCOUNT_STATE,
                                                                              null);

        try {
            adminService.updateApplication(cassandraApplication1.getApplicationId(), application);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(cassandraApplication1.getClientId()));
        }

        cassandraTemplate.insert(cassandraClient1);

        Observable<ApplicationUserDetails> returnedApplication = adminService.updateApplication(cassandraApplication1.getApplicationId(), application);
        assertNotNull(returnedApplication);
        final ApplicationUserDetails singleApplication = returnedApplication.toBlocking().single();

        assertEquals(application.getClientId(), singleApplication.getClientId());
        assertEquals(application.getName(), singleApplication.getName());
        assertEquals(application.getEmail(), singleApplication.getEmail());
        assertEquals(application.getState(), singleApplication.getState());
        assertNotNull(singleApplication.getApplicationId());

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
                QueryBuilder.eq(CassandraApplication.APPLICATION_ID, singleApplication.getApplicationId())).limit(10);

        final CassandraApplication cassandraApplicationResult = cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class);
        assertEquals(application.getClientId(), cassandraApplicationResult.getClientId());
        assertEquals(application.getName(), cassandraApplicationResult.getName());
        assertEquals(application.getEmail(), cassandraApplicationResult.getEmail());
        assertEquals(application.getState(), cassandraApplicationResult.getAccountState());
        assertNotNull(cassandraApplicationResult.getSecret());
        assertNotNull(cassandraApplicationResult.getCreatedTimestamp());
        assertNotNull(cassandraApplicationResult.getLastModifiedTimestamp());

        final Select clientSelect = QueryBuilder.select().from(CassandraClient.TABLE_NAME).where(
                QueryBuilder.eq(CassandraClient.CLIENT_ID, application.getClientId())).limit(10);

        final CassandraClient cassandraClientResult = cassandraTemplate.selectOne(clientSelect, CassandraClient.class);
        assertEquals(application.getClientId(), cassandraClientResult.getClientId());
        assertEquals(singleApplication.getApplicationId(), cassandraClientResult.getApplicationId());
        assertNotNull(cassandraClientResult.getCreatedTimestamp());
        assertNotNull(cassandraClientResult.getLastModifiedTimestamp());

        cassandraTemplate.delete(cassandraApplication1);
        cassandraTemplate.delete(cassandraClient1);
    }

    @Test
    public void testAddService() throws Exception {

        CassandraAllowedService cassandraAllowedService = TestUtils.createDummyCassAllowedService();
        cassandraAllowedService.setAllowedServiceId("newServiceId");
        final Select serviceSelect = QueryBuilder.select().from(CassandraAllowedService.TABLE_NAME).where(
                QueryBuilder.eq(CassandraAllowedService.ALLOWED_SERVICE_ID, cassandraAllowedService.getAllowedServiceId())).limit(10);

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
            QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication1.getApplicationId())).limit(10);

        try {
            adminService.addService(cassandraApplication1.getApplicationId(), SERVICE_ID);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(SERVICE_ID));
        }

        cassandraTemplate.insert(cassandraAllowedService);
        cassandraTemplate.insert(cassandraApplication1);

        Observable<ApplicationAllowedService> allowedServiceObservable =
            adminService.addService(cassandraApplication1.getApplicationId(), cassandraAllowedService.getAllowedServiceId());
        final ApplicationAllowedService returnedAppAllowedService = allowedServiceObservable.toBlocking().single();

        assertEquals(cassandraAllowedService.getAllowedServiceId(), returnedAppAllowedService.getServiceTypeId());
        assertThat(cassandraAllowedService.getAllowedServiceId(),
                   isIn(cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class).getAllowedServiceIds()));
        assertThat(cassandraApplication1.getApplicationId(),
                   isIn(cassandraTemplate.selectOne(serviceSelect, CassandraAllowedService.class).getApplicationIds()));

        cassandraTemplate.delete(cassandraAllowedService);
        cassandraTemplate.delete(cassandraApplication1);

        // Null Id fields
        cassandraAllowedService.setApplicationIds(null);
        cassandraApplication1.setAllowedServiceIds(null);
        cassandraTemplate.insert(cassandraAllowedService);
        cassandraTemplate.insert(cassandraApplication1);

        allowedServiceObservable = adminService.addService(
            cassandraApplication1.getApplicationId(), cassandraAllowedService.getAllowedServiceId());

        assertEquals(cassandraAllowedService.getAllowedServiceId(), allowedServiceObservable.toBlocking().single().getServiceTypeId());
        assertThat(cassandraAllowedService.getAllowedServiceId(),
                   isIn(cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class).getAllowedServiceIds()));
        assertThat(cassandraApplication1.getApplicationId(),
                   isIn(cassandraTemplate.selectOne(serviceSelect, CassandraAllowedService.class).getApplicationIds()));

        cassandraTemplate.delete(cassandraAllowedService);
        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testDeleteService() throws Exception {
        CassandraAllowedService cassandraAllowedService = TestUtils.createDummyCassAllowedService();
        final Select serviceSelect = QueryBuilder.select().from(CassandraAllowedService.TABLE_NAME).where(
                QueryBuilder.eq(CassandraAllowedService.ALLOWED_SERVICE_ID, cassandraAllowedService.getAllowedServiceId())).limit(10);

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
            QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication1.getApplicationId())).limit(10);

        try {
            adminService.addService(cassandraApplication1.getApplicationId(), SERVICE_ID);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(SERVICE_ID));
        }
        cassandraTemplate.insert(cassandraAllowedService);
        cassandraTemplate.insert(cassandraApplication1);

        adminService.deleteService(cassandraApplication1.getApplicationId(), cassandraAllowedService.getAllowedServiceId());

        assertThat(cassandraAllowedService.getAllowedServiceId(),
                   not(cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class).getAllowedServiceIds()));
        assertThat(cassandraApplication1.getApplicationId(),
                   not(cassandraTemplate.selectOne(serviceSelect, CassandraAllowedService.class).getApplicationIds()));

        cassandraTemplate.delete(cassandraAllowedService);
        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testAddRole() throws Exception {

        CassandraRole cassandraRole = TestUtils.createDummyCassRole();
        cassandraRole.setRoleId("newRoleId");
        final Select roleSelect = QueryBuilder.select().from(CassandraRole.TABLE_NAME).where(
                QueryBuilder.eq(CassandraRole.ROLE_ID, cassandraRole.getRoleId())).limit(10);

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
            QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication1.getApplicationId())).limit(10);

        try {
            adminService.addRole(cassandraApplication1.getApplicationId(), ROLE_ADMIN);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(APPLICATION_ID_1.toString()));
        }

        cassandraTemplate.insert(cassandraRole);
        cassandraTemplate.insert(cassandraApplication1);

        Observable<Role> roleObservable =
            adminService.addRole(cassandraApplication1.getApplicationId(), cassandraRole.getRoleId());
        assertEquals(cassandraRole.getRoleId(), roleObservable.toBlocking().single().getRoleId());
        assertThat(cassandraApplication1.getApplicationId(), isIn(roleObservable.toBlocking().single().getApplicationIds()));
        assertThat(cassandraRole.getRoleId(),
                   isIn(cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class).getRoleIds()));
        assertThat(cassandraApplication1.getApplicationId(),
                   isIn(cassandraTemplate.selectOne(roleSelect, CassandraRole.class).getApplicationIds()));

        cassandraTemplate.delete(cassandraRole);
        cassandraTemplate.delete(cassandraApplication1);

        // Null Id fields
        cassandraRole.setApplicationIds(null);
        cassandraApplication1.setRoleIds(null);
        cassandraTemplate.insert(cassandraRole);
        cassandraTemplate.insert(cassandraApplication1);

        roleObservable = adminService.addRole(cassandraApplication1.getApplicationId(), cassandraRole.getRoleId());
        assertEquals(cassandraRole.getRoleId(), roleObservable.toBlocking().single().getRoleId());
        assertThat(cassandraApplication1.getApplicationId(), isIn(roleObservable.toBlocking().single().getApplicationIds()));
        assertThat(cassandraRole.getRoleId(),
                   isIn(cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class).getRoleIds()));
        assertThat(cassandraApplication1.getApplicationId(),
                   isIn(cassandraTemplate.selectOne(roleSelect, CassandraRole.class).getApplicationIds()));

        cassandraTemplate.delete(cassandraRole);
        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testDeleteRole() throws Exception {
        CassandraRole cassandraRole = TestUtils.createDummyCassRole();
        final Select roleSelect = QueryBuilder.select().from(CassandraRole.TABLE_NAME).where(
                QueryBuilder.eq(CassandraRole.ROLE_ID, cassandraRole.getRoleId())).limit(10);

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
            QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication1.getApplicationId())).limit(10);

        try {
            adminService.deleteRole(cassandraApplication1.getApplicationId(), ROLE_ADMIN);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(APPLICATION_ID_1.toString()));
        }

        cassandraTemplate.insert(cassandraRole);
        cassandraTemplate.insert(cassandraApplication1);
        adminService.deleteRole(cassandraApplication1.getApplicationId(), cassandraRole.getRoleId());

        assertThat(cassandraRole.getApplicationIds(),
                   not(cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class).getAllowedServiceIds()));
        assertThat(cassandraApplication1.getApplicationId(),
                   not(cassandraTemplate.selectOne(roleSelect, CassandraRole.class).getApplicationIds()));

        cassandraTemplate.delete(cassandraRole);
        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testGetAllServiceTypes() throws Exception {

        when(mockIServiceRegistration.getAllRegisteredServices()).thenReturn(null);

        Observable<ServiceTypeList> serviceTypeObservable;
        try {
            serviceTypeObservable = adminService.getAllServiceTypes();
        } catch (AdminServiceNoTypesException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString("Service"));
        }

        when(mockIServiceRegistration.getAllRegisteredServices()).thenReturn(SERVICE_TYPES);
        serviceTypeObservable = adminService.getAllServiceTypes();
        assertEquals(1, serviceTypeObservable.toBlocking().single().getServiceTypes().size());
    }

    @Test
    public void testGetAllRoles() throws Exception {
        CassandraRole cassandraRole1 = TestUtils.createDummyCassRole();
        cassandraRole1.setRoleId("newRoleId1");
        CassandraRole cassandraRole2 = TestUtils.createDummyCassRole();
        cassandraRole2.setRoleId("newRoleId2");

        Observable<RoleList> roleObservable = adminService.getAllRoles();
        assertTrue(roleObservable.toBlocking().single().getRoles().isEmpty());

        cassandraTemplate.insert(cassandraRole1);
        cassandraTemplate.insert(cassandraRole2);

        roleObservable = adminService.getAllRoles();
        assertEquals(2, roleObservable.toBlocking().single().getRoles().size());

        cassandraTemplate.delete(cassandraRole1);
        cassandraTemplate.delete(cassandraRole2);
    }

    @Test
    public void testGetAllProtocolTypes() throws Exception {
        CassandraProtocolType cassandraProtocolType1 = TestUtils.createDummyCassProtocolType();
        cassandraProtocolType1.setProtocolTypeId(TestUtils.PROTOCOL_TYPE_ID);
        CassandraProtocolType cassandraProtocolType2 = TestUtils.createDummyCassProtocolType();
        cassandraProtocolType2.setProtocolTypeId(TestUtils.PROTOCOL_TYPE_ID_2);

        Observable<ProtocolTypeList> protocolTypeObservable = adminService.getAllProtocolTypes();
        assertTrue(protocolTypeObservable.toBlocking().single().getProtocolTypes().isEmpty());

        cassandraTemplate.insert(cassandraProtocolType1);
        cassandraTemplate.insert(cassandraProtocolType2);

        protocolTypeObservable = adminService.getAllProtocolTypes();
        assertEquals(2, protocolTypeObservable.toBlocking().single().getProtocolTypes().size());

        cassandraTemplate.delete(cassandraProtocolType1);
        cassandraTemplate.delete(cassandraProtocolType2);
    }


    @Test
    public void testAddApplicationPolicy() throws Exception {
        Observable<ApplicationPolicy> applicationPolicyObservable;
        final UUID randomId = UUID.randomUUID();

        try {
            applicationPolicyObservable = adminService.addApplicationPolicy(randomId, POLICY_ID, APPLICATION_POLICY);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(randomId.toString()));
        }

        // add cassandraApp
        cassandraTemplate.insert(cassandraApplication1);
        when(mockIServiceRegistration.findApplicationPolicyById(POLICY_ID)).thenReturn(null);
        try {
            applicationPolicyObservable = adminService.addApplicationPolicy(APPLICATION_ID_1, POLICY_ID, APPLICATION_POLICY);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(POLICY_ID));
        }

        // test keys
        when(mockIServiceRegistration.findApplicationPolicyById(POLICY_ID)).thenReturn(POLICY_META_DATA);
        try {
            applicationPolicyObservable = adminService.addApplicationPolicy(APPLICATION_ID_1, POLICY_ID, APPLICATION_POLICY_2);
        } catch (AdminServiceInvalidPolicyKeysException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(KEY));
        }

        when(typeMapper.toPolicies(cassandraApplication1.getGatewayPolicies())).thenReturn(policySet);
        when(adminServiceTypeMapper.toApplicationPolicy(POLICY)).thenReturn(APPLICATION_POLICY);
        applicationPolicyObservable = adminService.addApplicationPolicy(APPLICATION_ID_1, POLICY_ID, APPLICATION_POLICY);
        assertEquals(POLICY_ID, applicationPolicyObservable.toBlocking().single().getPolicyId());

        when(typeMapper.toPolicies(cassandraApplication1.getGatewayPolicies())).thenReturn(null);
        applicationPolicyObservable = adminService.addApplicationPolicy(APPLICATION_ID_1, POLICY_ID, APPLICATION_POLICY);
        assertEquals(POLICY_ID, applicationPolicyObservable.toBlocking().single().getPolicyId());

        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testDeleteApplicationPolicy() throws Exception {
        final UUID randomId = UUID.randomUUID();

        try {
            adminService.deleteApplicationPolicy(randomId, POLICY_ID);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(randomId.toString()));
        }

        // add cassandraApp
        cassandraApplication1.setGatewayPolicies(null);
        cassandraTemplate.insert(cassandraApplication1);

        try {
            adminService.deleteApplicationPolicy(APPLICATION_ID_1, POLICY_ID);
        } catch (AdminServiceNoTypesException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(ApplicationAdminService.POLICY));
        }

        cassandraApplication1.setGatewayPolicies(POLICIES_STRING);
        cassandraTemplate.insert(cassandraApplication1);
        when(typeMapper.toPolicies(POLICIES_STRING)).thenReturn(policySet);

        try {
            adminService.deleteApplicationPolicy(APPLICATION_ID_1, POLICY_ID_2);
        } catch (AdminServiceNoTypesException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(ApplicationAdminService.POLICY));
        }

        adminService.deleteApplicationPolicy(APPLICATION_ID_1, POLICY_ID);

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
            QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication1.getApplicationId())).limit(10);

        final CassandraApplication cassandraApplicationResult = cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class);
        assertNull(cassandraApplicationResult.getGatewayPolicies());
        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testAddServicePolicy() throws Exception {
        Observable<ApplicationPolicy> applicationPolicyObservable;
        final UUID randomId = UUID.randomUUID();

        try {
            applicationPolicyObservable = adminService.addServicePolicy(randomId, SERVICE_ID, POLICY_ID, APPLICATION_POLICY);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(randomId.toString()));
        }

        cassandraTemplate.insert(cassandraApplication1);

        when(mockIServiceRegistration.findServiceByServiceId(SERVICE_ID)).thenReturn(null);
        try {
            applicationPolicyObservable = adminService.addServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID, APPLICATION_POLICY);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(SERVICE_ID));
        }

        when(mockIServiceRegistration.findServiceByServiceId(SERVICE_ID)).thenReturn(SERVICE_TYPE);
        when(mockIServiceRegistration.findApplicationPolicyById(POLICY_ID)).thenReturn(null);
        try {
            applicationPolicyObservable = adminService.addServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID, APPLICATION_POLICY);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(POLICY_ID));
        }

        when(mockIServiceRegistration.findApplicationPolicyById(POLICY_ID)).thenReturn(POLICY_META_DATA);
        try {
            applicationPolicyObservable = adminService.addServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID, APPLICATION_POLICY_2);
        } catch (AdminServiceInvalidPolicyKeysException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(KEY));
        }
        when(typeMapper.toPolicies(cassandraApplication1.getGatewayPolicies())).thenReturn(policySet);
        when(adminServiceTypeMapper.toApplicationPolicy(POLICY)).thenReturn(APPLICATION_POLICY);
        applicationPolicyObservable = adminService.addServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID, APPLICATION_POLICY);
        assertEquals(POLICY_ID, applicationPolicyObservable.toBlocking().single().getPolicyId());

        when(typeMapper.toPolicies(cassandraApplication1.getGatewayPolicies())).thenReturn(null);
        applicationPolicyObservable = adminService.addServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID, APPLICATION_POLICY);
        assertEquals(POLICY_ID, applicationPolicyObservable.toBlocking().single().getPolicyId());

        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testDeleteServicePolicy() throws Exception {
        final UUID randomId = UUID.randomUUID();

        try {
            adminService.deleteServicePolicy(randomId, SERVICE_ID, POLICY_ID);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(randomId.toString()));
        }

        cassandraApplication1.setPolicies(null);
        cassandraApplication1.setAllowedServiceIds(null);
        cassandraTemplate.insert(cassandraApplication1);

        try {
            adminService.deleteServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID);
        } catch (AdminServiceEntityNotFoundException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(APPLICATION_ID_1.toString()));
        }

        cassandraApplication1.setAllowedServiceIds(SERVICE_IDS);
        cassandraTemplate.insert(cassandraApplication1);

        try {
            adminService.deleteServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID);
        } catch (AdminServiceNoTypesException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(ApplicationAdminService.POLICY));
        }

        cassandraApplication1.setPolicies(SERVICE_POLICIES_STRING);
        cassandraTemplate.insert(cassandraApplication1);
        when(typeMapper.toPolicies(SERVICE_POLICIES_STRING)).thenReturn(policySet);

        try {
            adminService.deleteServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID_2);
        } catch (AdminServiceNoTypesException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString(ApplicationAdminService.POLICY));
        }

        adminService.deleteServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID);

        final Select applicationSelect = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
            QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication1.getApplicationId())).limit(10);

        final CassandraApplication cassandraApplicationResult = cassandraTemplate.selectOne(applicationSelect, CassandraApplication.class);
        assertNull(cassandraApplicationResult.getPolicies());
        cassandraTemplate.delete(cassandraApplication1);
    }

    @Test
    public void testGetAllPolicyTypes() throws Exception {

        when(mockIServiceRegistration.getAllApplicationPolicies()).thenReturn(null);
        when(mockIServiceRegistration.getAllRegisteredServices()).thenReturn(null);

        Observable<PolicyTypeList> policyTypeListObservable;
        try {
            policyTypeListObservable = adminService.getAllPolicyTypes();
        } catch (AdminServiceNoTypesException e) {
            logger.debug(e.getMessage());
            assertThat(e.getMessage(), containsString("Policy"));
        }

        when(mockIServiceRegistration.getAllApplicationPolicies()).thenReturn(POLICY_META_DATAS);
        when(mockIServiceRegistration.getAllRegisteredServices()).thenReturn(null);
        when(adminServiceTypeMapper.toPolicyTypeList(POLICY_META_DATAS)).thenReturn(POLICY_TYPES);

        policyTypeListObservable = adminService.getAllPolicyTypes();
        assertEquals(1, policyTypeListObservable.toBlocking().single().getPolicyTypes().size());

        when(mockIServiceRegistration.getAllApplicationPolicies()).thenReturn(null);
        when(mockIServiceRegistration.getAllRegisteredServices()).thenReturn(SERVICE_TYPES);
        policyTypeListObservable = adminService.getAllPolicyTypes();
        assertEquals(1, policyTypeListObservable.toBlocking().single().getPolicyTypes().size());
    }
}

package com.vennetics.bell.sam.admin.service.userdetails.command;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.vennetics.bell.sam.admin.service.repositories.ClientRepository;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GetUuidByClientIdCommandTest {

    @Mock
    private ClientRepository clientRepository;


    private GetUuidByClientIdCommand testClass;

    private static final String CLIENT_ID = "clientId";

    private static final UUID APP_ID = UUID.randomUUID();

    private static final CassandraClient CASSANDRA_CLIENT = new CassandraClient(CLIENT_ID, APP_ID);

    private HystrixRequestContext context;

    @Before
    public void init() {
        context = HystrixRequestContext.initializeContext();
        testClass = new GetUuidByClientIdCommand(clientRepository, CLIENT_ID);
    }

    @After
    public void destroy() {
        context.shutdown();
    }

    @Test
    public void shouldQueryRepoOnExecution() {


        when(clientRepository.findOne(CLIENT_ID)).thenReturn(CASSANDRA_CLIENT);

        final UUID result = testClass.execute();

        assertThat(result, equalTo(APP_ID));
    }

    @Test
    public void shouldReturnNullWhenNotFound() {

        when(clientRepository.findOne(CLIENT_ID)).thenReturn(null);

        final UUID result = testClass.execute();

        assertThat(result, nullValue());
    }

    @Test
    public void shouldNotCacheResultForMultipleExecutions() {

        when(clientRepository.findOne(CLIENT_ID)).thenReturn(CASSANDRA_CLIENT);

        assertThat(new GetUuidByClientIdCommand(clientRepository, CLIENT_ID).execute(),
                   equalTo(APP_ID));
        assertThat(new GetUuidByClientIdCommand(clientRepository, CLIENT_ID).execute(),
                   equalTo(APP_ID));
        assertThat(new GetUuidByClientIdCommand(clientRepository, CLIENT_ID).execute(),
                   equalTo(APP_ID));

        verify(clientRepository, times(3)).findOne(CLIENT_ID);
    }

}

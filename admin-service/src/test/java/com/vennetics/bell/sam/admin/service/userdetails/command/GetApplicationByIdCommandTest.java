package com.vennetics.bell.sam.admin.service.userdetails.command;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.vennetics.bell.sam.admin.service.repositories.ApplicationRepository;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GetApplicationByIdCommandTest {

    @Mock
    private ApplicationRepository applicationRepository;

    private GetApplicationByIdCommand testClass;

    private static final UUID APP_ID = UUID.randomUUID();

    private static final CassandraApplication CASSANDRA_APPLICATION = new CassandraApplication();

    private HystrixRequestContext context;

    @Before
    public void init() {
        context = HystrixRequestContext.initializeContext();
        testClass = new GetApplicationByIdCommand(applicationRepository, APP_ID);
    }

    @After
    public void destroy() {
        context.shutdown();
    }

    @Test
    public void shouldQueryRepoOnExecution() {

        when(applicationRepository.findOne(APP_ID)).thenReturn(CASSANDRA_APPLICATION);

        final CassandraApplication result = testClass.execute();

        assertThat(result, equalTo(CASSANDRA_APPLICATION));
    }

    @Test
    public void shouldReturnNullWhenNotFound() {

        when(applicationRepository.findOne(APP_ID)).thenReturn(null);

        final CassandraApplication result = testClass.execute();

        assertThat(result, nullValue());
    }

    @Test
    public void shouldNotCacheResultForMultipleExecutions() {

        when(applicationRepository.findOne(APP_ID)).thenReturn(CASSANDRA_APPLICATION);

        assertThat(new GetApplicationByIdCommand(applicationRepository, APP_ID).execute(),
                   equalTo(CASSANDRA_APPLICATION));
        assertThat(new GetApplicationByIdCommand(applicationRepository, APP_ID).execute(),
                   equalTo(CASSANDRA_APPLICATION));
        assertThat(new GetApplicationByIdCommand(applicationRepository, APP_ID).execute(),
                   equalTo(CASSANDRA_APPLICATION));

        verify(applicationRepository, times(3)).findOne(APP_ID);
    }

}

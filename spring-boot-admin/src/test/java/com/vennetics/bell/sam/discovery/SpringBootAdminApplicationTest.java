package com.vennetics.bell.sam.discovery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import de.codecentric.boot.admin.discovery.ApplicationDiscoveryListener;
import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.registry.ApplicationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class SpringBootAdminApplicationTest {

    @Mock private DiscoveryClient mockDiscoveryClient;
    @Mock private ApplicationRegistry mockApplicationRegistry;
    @Mock private ServiceInstance mockServiceInstance1;
    @Mock private ServiceInstance mockServiceInstance2;
    @Mock private ServiceInstance mockServiceInstance3;

    @Test
    public void shouldConvertServiceInstances() throws Exception {

        final SpringBootAdminApplication mainline = new SpringBootAdminApplication();
        mainline.setContextPathApplications("a1,a2");

        when(mockDiscoveryClient.getServices()).thenReturn(Arrays.asList("A1", "A2", "A3"));
        when(mockDiscoveryClient.getInstances(anyString()))
                .thenReturn(Arrays.asList(mockServiceInstance1))
                .thenReturn(Arrays.asList(mockServiceInstance2))
                .thenReturn(Arrays.asList(mockServiceInstance3));
        when(mockServiceInstance1.getServiceId()).thenReturn("A1");
        when(mockServiceInstance2.getServiceId()).thenReturn("A2");
        when(mockServiceInstance3.getServiceId()).thenReturn("A3");
        when(mockServiceInstance1.getUri()).thenReturn(new URI("http://localhost"));
        when(mockServiceInstance2.getUri()).thenReturn(new URI("http://localhost"));
        when(mockServiceInstance3.getUri()).thenReturn(new URI("http://localhost"));

        final ApplicationDiscoveryListener listener =
                                    mainline.customDiscoveryListener(mockDiscoveryClient, mockApplicationRegistry);

        listener.onInstanceRegistered(null);

        final ArgumentCaptor<Application> applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);
        verify(mockApplicationRegistry, times(3)).register(applicationArgumentCaptor.capture());

        assertThat(applicationArgumentCaptor.getAllValues().get(0).getHealthUrl(), is("http://localhost/a1/health"));
        assertThat(applicationArgumentCaptor.getAllValues().get(1).getHealthUrl(), is("http://localhost/a2/health"));
        assertThat(applicationArgumentCaptor.getAllValues().get(2).getHealthUrl(), is("http://localhost/health"));
    }
}

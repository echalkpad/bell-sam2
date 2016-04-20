package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter.commands;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.model.subscriber.thirdparty.Attribute;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;

public class GetSubscriberProfileCommandTest {

    @Mock
    private RestTemplate mockTemplate;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetSubscriberProfile() throws Exception {
        final String expectedUrl = "http://subx/api/subscriber/profile?searchFilter=SUBID&filterValue=VALUE";
        final ResponseEntity<SubscriberProfile> dummyResult =
                new ResponseEntity<>(
                        new SubscriberProfile(Collections.singletonList(new Attribute("SUBID", "123456789"))),
                        HttpStatus.ACCEPTED);
        when(mockTemplate.getForEntity(expectedUrl, SubscriberProfile.class)).thenReturn(dummyResult);

        final GetSubscriberProfileCommand command = new GetSubscriberProfileCommand("subx",
                                                                                    "SUBID",
                                                                                    "VALUE",
                                                                                    mockTemplate);

        assertThat(command.execute(), sameInstance(dummyResult));
    }
}

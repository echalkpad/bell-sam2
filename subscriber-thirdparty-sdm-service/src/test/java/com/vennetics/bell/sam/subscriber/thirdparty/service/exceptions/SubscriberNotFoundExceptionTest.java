package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.vennetics.bell.sam.sdm.adapter.api.SdmConstants;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

public class SubscriberNotFoundExceptionTest {

    private SdmQueryFilter ouQueryFilter;
    private SdmQueryFilter mdnQueryFilter;
    private Set<SdmQueryFilter> sdmQueryFilters;

    private static final String AFFILIATE_ID = "022";

    private static final String TEN_DIGITS = "4161112222";

    @Before
    public void setUp() throws Exception {
        ouQueryFilter = new SdmQueryFilter(SdmConstants.ATTR_OU, AFFILIATE_ID);
        mdnQueryFilter = new SdmQueryFilter(SdmConstants.ATTR_MDN, TEN_DIGITS);
        sdmQueryFilters = new HashSet<>(Arrays.asList(ouQueryFilter, mdnQueryFilter));
    }

    @Test
    public void shouldProcessSdmQueryFiltersAsExpected() {
        final SubscriberNotFoundException exception = new SubscriberNotFoundException(sdmQueryFilters);
        assertNotNull(exception);
        assertThat(exception.getMessageArguments(),
                   hasItemInArray(ouQueryFilter.getFilterAttribute()));
        assertThat(exception.getMessageArguments(), hasItemInArray(ouQueryFilter.getFilterValue()));
        assertThat(exception.getMessageArguments(),
                   hasItemInArray(mdnQueryFilter.getFilterAttribute()));
        assertThat(exception.getMessageArguments(),
                   hasItemInArray(mdnQueryFilter.getFilterValue()));
    }

}

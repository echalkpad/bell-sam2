package com.vennetics.bell.sam.sdm.adapter.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class SdmQueryFilterTest {

    @Test
    public void shouldImplementEqualsAndHashCode() {
        EqualsVerifier.forClass(SdmQueryFilter.class).verify();
    }

    @Test
    public void shouldSupportToString() {
        final SdmQueryFilter filter = new SdmQueryFilter(SdmConstants.ATTR_IMSI, "imsi");
        assertThat(filter.getFilterAttribute(), equalTo(SdmConstants.ATTR_IMSI));
        assertThat(filter.getFilterValue(), equalTo("imsi"));
        assertThat(filter.toString(), containsString("filterAttribute=" + SdmConstants.ATTR_IMSI));
        assertThat(filter.toString(), containsString("filterValue=imsi"));
    }

}

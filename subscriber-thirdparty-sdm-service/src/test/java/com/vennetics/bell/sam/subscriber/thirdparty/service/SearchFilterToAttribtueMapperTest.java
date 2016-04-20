package com.vennetics.bell.sam.subscriber.thirdparty.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.sdm.adapter.api.SdmConstants;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.InvalidFilterTypeException;

public class SearchFilterToAttribtueMapperTest {

    @Test
    public void shouldMapKnownAttributes() {
        assertEquals(SdmConstants.ATTR_MDN,
                     SearchFilterToAttribtueMapper.searchFilterToAttribute(SearchFilterType.MDN));

        assertEquals(SdmConstants.ATTR_IMSI,
                     SearchFilterToAttribtueMapper.searchFilterToAttribute(SearchFilterType.IMSI));

        assertEquals(SdmConstants.ATTR_MEID,
                     SearchFilterToAttribtueMapper.searchFilterToAttribute(SearchFilterType.MEID));

        assertEquals(SdmConstants.ATTR_SUB_ID,
                     SearchFilterToAttribtueMapper.searchFilterToAttribute(SearchFilterType.SUBID));

        assertEquals(SdmConstants.ATTR_UUID,
                     SearchFilterToAttribtueMapper.searchFilterToAttribute(SearchFilterType.UUID));
    }

    @Test(expected = InvalidFilterTypeException.class)
    public void shouldFailUnnownAttributes() {

        SearchFilterToAttribtueMapper.searchFilterToAttribute(null);
    }

}

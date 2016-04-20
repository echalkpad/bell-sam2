package com.vennetics.bell.sam.sdm.adapter.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SdmConstantsTest {

    @Test
    public void shouldReturnCorrectConstantValue() {
        assertEquals(SdmConstants.ATTR_EAM_LANGUAGE, "eamLanguage");
        assertEquals(SdmConstants.ATTR_FEATURE_CODE, "featureCode");
        assertEquals(SdmConstants.ATTR_MDN, "mdn");
        assertEquals(SdmConstants.ATTR_MEID, "meid");
        assertEquals(SdmConstants.ATTR_MIN, "min");
        assertEquals(SdmConstants.ATTR_NETWORKID, "networkid");
        assertEquals(SdmConstants.ATTR_OU, "ou");
        assertEquals(SdmConstants.ATTR_PPD_PROC_INDICATOR, "ppdProcIndicator");
        assertEquals(SdmConstants.ATTR_SUB_ID, "subId2");
        assertEquals(SdmConstants.ATTR_UA_PROF_URL, "UAProfURL");
        assertEquals(SdmConstants.ATTR_USERAGENT, "useragent");
    }

}

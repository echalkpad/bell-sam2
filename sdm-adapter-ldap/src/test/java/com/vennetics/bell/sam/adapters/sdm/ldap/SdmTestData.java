package com.vennetics.bell.sam.adapters.sdm.ldap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;

/**
 * Static data used repeatedly in unit tests.
 */
public final class SdmTestData {

    public static final String MDN_VALUE = "4161234567";
    public static final String OU_BELL = "023";
    public static final String MDN_ATTR = "mdn";
    public static final String OU_ATTR = "ou";
    private static final Map<String, Set<String>> SDM_ACCOUNT_MAP = new HashMap<>();

    static {
        SDM_ACCOUNT_MAP.put(OU_ATTR, new HashSet<>(Arrays.asList(OU_BELL)));
        SDM_ACCOUNT_MAP.put(MDN_ATTR, new HashSet<>(Arrays.asList(MDN_VALUE)));
    }

    public static final SdmAccount SDM_ACCOUNT = new SdmAccount(SDM_ACCOUNT_MAP);

    private SdmTestData() {

    }
}

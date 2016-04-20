package com.vennetics.bell.sam.subscriber.thirdparty.service;

import com.vennetics.bell.sam.core.exception.PlatformFailedException;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.sdm.adapter.api.SdmConstants;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.InvalidFilterTypeException;

/**
 * Maps API values to internal values e.g. SDM attributes.
 */
public final class SearchFilterToAttribtueMapper {

    private SearchFilterToAttribtueMapper() {

    }

    /**
     * Mapper function
     *
     * @param filterType
     *            the API filter type.
     * @return the SDM attribute string
     */
    public static String searchFilterToAttribute(final SearchFilterType filterType) {
        if (filterType == null) {
            throw new InvalidFilterTypeException(null);
        }

        final String result = toSdmAttrAsString(filterType);

        if (result == null) {
            // Unreachable
            throw new PlatformFailedException("Unsupported SearchFilterType:" + filterType);
        }

        return result;
    }

    private static String toSdmAttrAsString(final SearchFilterType filterType) {
        String result;
        switch (filterType) {
            case IMSI:
                result = SdmConstants.ATTR_IMSI;
                break;
            case MDN:
                result = SdmConstants.ATTR_MDN;
                break;
            case MEID:
                result = SdmConstants.ATTR_MEID;
                break;
            case SUBID:
                result = SdmConstants.ATTR_SUB_ID;
                break;
            case UUID:
                result = SdmConstants.ATTR_UUID;
                break;
            default:
                result = null;
        }

        return result;
    }
}

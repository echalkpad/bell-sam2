package com.vennetics.bell.sam.subscriber.thirdparty.service;

import java.beans.PropertyEditorSupport;

import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.InvalidFilterTypeException;

/**
 * Validates and Maps search filter type. Here to allow correct error when
 * invalid value supplied.
 */
public class SearchFilterTypePropertyEditor extends PropertyEditorSupport {


    @Override
    public void setAsText(final String text) {

        try {
            setValue(SearchFilterType.valueOf(text));
        } catch (final RuntimeException e) {
            throw new InvalidFilterTypeException(e, text);
        }
    }
}

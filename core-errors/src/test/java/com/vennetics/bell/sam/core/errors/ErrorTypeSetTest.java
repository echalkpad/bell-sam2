package com.vennetics.bell.sam.core.errors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.vennetics.testPackage.AnnotatedErrorType;

public class ErrorTypeSetTest {

    @Test
    public void shouldDefineFilename() {

        final Class<AnnotatedErrorType> clazz = AnnotatedErrorType.class;
        final ErrorTypeSet annotation = clazz.getAnnotation(ErrorTypeSet.class);
        assertThat(annotation.value(), is("errors.yml"));
    }
}

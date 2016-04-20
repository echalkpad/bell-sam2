package com.vennetics.bell.sam.core.errors;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.vennetics.testPackage.AnnotatedTestMessageType1;
import com.vennetics.testPackage.AnnotatedTestMessageType2;

@RunWith(MockitoJUnitRunner.class)
public class ErrorDictionaryLoaderTest {

    @Autowired
    @InjectMocks
    private ErrorDictionaryLoader loader;

    @Mock private IErrorSetLoader errorSetLoader;

    @Test
    public void shouldLoadAllErrorSets() throws Exception {
        loader.setBasePackage("com.vennetics.testPackage");
        final IErrorDictionary dictionary = loader.loadAllErrorSets();
        assertThat(dictionary, is(not(nullValue())));

        verify(errorSetLoader).loadErrorSet(isA(ErrorDictionary.class), eq("setloadertest1.yml"), aryEq(AnnotatedTestMessageType1.values()));

        verify(errorSetLoader).loadErrorSet(isA(ErrorDictionary.class), eq("setloadertest2.yml"), aryEq(AnnotatedTestMessageType2.values()));

        verify(errorSetLoader, never()).loadErrorSet(isA(ErrorDictionary.class), eq("setloadertest3.yml"), anyObject());
    }
}

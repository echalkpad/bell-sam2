package com.vennetics.testPackage;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

/**
 * Error message types for testing {@link com.vennetics.bell.sam.core.errors.ErrorDictionaryLoader}.
 */
@ErrorTypeSet("setloadertest1.yml")
public enum AnnotatedTestMessageType1 {

        TEST_MESSAGE_1,
        TEST_MESSAGE_2,
        TEST_MESSAGE_3;
}

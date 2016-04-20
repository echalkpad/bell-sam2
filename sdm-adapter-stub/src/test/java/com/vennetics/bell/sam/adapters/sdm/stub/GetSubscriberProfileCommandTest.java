package com.vennetics.bell.sam.adapters.sdm.stub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;

import junit.framework.TestCase;
import rx.Observable;

/**
 * The test class for GetSubscriberProfileCommand
 *
 * Created by aaronwatters on 04/01/2016.
 */
public class GetSubscriberProfileCommandTest extends TestCase {


    private Map<String, Set<String>> expectedResult;

    @Override
    @Before
    public void setUp() throws Exception {

        final Set<String> eamLanguageAttributes = new HashSet<>();
        eamLanguageAttributes.add("E");


        expectedResult = new HashMap<>();

        expectedResult.put("eamLanguage", eamLanguageAttributes);


    }

    @Test
    public void testRun() throws Exception {

        final Observable<List<SdmAccount>> getSubProfileCommand = new GetSubscriberProfileCommand().observe();

        // blocking
        assertEquals("The GetSubscriberProfileCommand's run method returned unexpected values",
                     new SdmAccount(expectedResult).getAttributes(),
                     getSubProfileCommand.toBlocking().single().get(0).getAttributes());
    }
}

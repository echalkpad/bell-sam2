package com.vennetics.bell.sam.adapters.sdm.ldap.support;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.junit.Test;

import com.vennetics.bell.sam.adapters.ldap.exception.LdapRequestException;
import com.vennetics.bell.sam.adapters.sdm.ldap.SdmTestData;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;

public class SdmAccountAttributesMapperTest {


    @Test
    public void shouldMapAttribtuesToAccount() throws Exception {

        final Set<String> attributes = SdmTestData.SDM_ACCOUNT.getAttributes().keySet();

        final SdmAccountAttributesMapper testClass = new SdmAccountAttributesMapper(attributes);

        final Attributes attrs = new BasicAttributes();

        final BasicAttribute ou = new BasicAttribute(SdmTestData.OU_ATTR);
        ou.add(SdmTestData.OU_BELL);
        attrs.put(ou);
        final BasicAttribute mdn = new BasicAttribute(SdmTestData.MDN_ATTR);
        mdn.add(SdmTestData.MDN_VALUE);
        attrs.put(mdn);

        final SdmAccount account = testClass.mapFromAttributes(attrs);

        assertEquals(SdmTestData.SDM_ACCOUNT.getAttributes(), account.getAttributes());
    }

    @Test
    public void shouldOnlyMapAttributesInQuery() throws Exception {

        final Set<String> attributes = new HashSet<>(Arrays.asList(SdmTestData.MDN_ATTR));

        final SdmAccountAttributesMapper testClass = new SdmAccountAttributesMapper(attributes);

        final Attributes attrs = new BasicAttributes();

        final BasicAttribute ou = new BasicAttribute(SdmTestData.OU_ATTR);
        ou.add(SdmTestData.OU_BELL);
        attrs.put(ou);
        final BasicAttribute mdn = new BasicAttribute(SdmTestData.MDN_ATTR);
        mdn.add(SdmTestData.MDN_VALUE);
        attrs.put(mdn);

        final SdmAccount account = testClass.mapFromAttributes(attrs);

        assertNull(account.getAttributes().get(SdmTestData.OU_ATTR));
        assertEquals(SdmTestData.MDN_VALUE,
                     account.getAttributes().get(SdmTestData.MDN_ATTR).iterator().next());
    }

    @Test
    public void shouldMapAllAttributesIfAttributesAreNull() throws Exception {

        final Set<String> attributes = null;

        final SdmAccountAttributesMapper testClass = new SdmAccountAttributesMapper(attributes);

        final Attributes attrs = new BasicAttributes();

        final BasicAttribute ou = new BasicAttribute(SdmTestData.OU_ATTR);
        ou.add(SdmTestData.OU_BELL);
        attrs.put(ou);
        final BasicAttribute mdn = new BasicAttribute(SdmTestData.MDN_ATTR);
        mdn.add(SdmTestData.MDN_VALUE);
        attrs.put(mdn);

        final SdmAccount account = testClass.mapFromAttributes(attrs);

        assertThat(account.getAttributes().get(SdmTestData.OU_ATTR), contains(SdmTestData.OU_BELL));

        assertThat(account.getAttributes().get(SdmTestData.MDN_ATTR),
                   contains(SdmTestData.MDN_VALUE));
    }

    @Test
    public void shouldMapAllAttributesIfAttributesIsEmpty() throws Exception {

        final Set<String> attributes = new HashSet<>();

        final SdmAccountAttributesMapper testClass = new SdmAccountAttributesMapper(attributes);

        final Attributes attrs = new BasicAttributes();

        final BasicAttribute ou = new BasicAttribute(SdmTestData.OU_ATTR);
        ou.add(SdmTestData.OU_BELL);
        attrs.put(ou);
        final BasicAttribute mdn = new BasicAttribute(SdmTestData.MDN_ATTR);
        mdn.add(SdmTestData.MDN_VALUE);
        attrs.put(mdn);

        final SdmAccount account = testClass.mapFromAttributes(attrs);

        assertThat(account.getAttributes().get(SdmTestData.OU_ATTR), contains(SdmTestData.OU_BELL));

        assertThat(account.getAttributes().get(SdmTestData.MDN_ATTR),
                   contains(SdmTestData.MDN_VALUE));
    }

    @Test(expected = LdapRequestException.class)
    public void shouldTranslateNamingExceptionToLdapRequestError() throws Exception {

        final Set<String> attributes = SdmTestData.SDM_ACCOUNT.getAttributes().keySet();

        final SdmAccountAttributesMapper testClass = new SdmAccountAttributesMapper(attributes) {

            @Override
            protected Set<String> getAttributes(final Attributes attrs,
                                                final String attributeName) throws NamingException {

                throw new NamingException("");
            }
        };

        final Attributes attrs = new BasicAttributes();

        final BasicAttribute ou = new BasicAttribute(SdmTestData.OU_ATTR);
        ou.add(SdmTestData.OU_BELL);
        attrs.put(ou);
        final BasicAttribute mdn = new BasicAttribute(SdmTestData.MDN_ATTR);
        mdn.add(SdmTestData.MDN_VALUE);
        attrs.put(mdn);

        testClass.mapFromAttributes(attrs);

    }
}

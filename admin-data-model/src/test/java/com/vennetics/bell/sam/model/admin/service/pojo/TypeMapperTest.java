package com.vennetics.bell.sam.model.admin.service.pojo;

import com.vennetics.bell.sam.core.exception.PlatformFailedException;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.APPLICATION_POLICY1;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.APP_POLICY_SET;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY1;
import static com.vennetics.bell.sam.model.admin.service.pojo.util.AdminDataModelTestData.SERVICE_POLICY_SET;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class TypeMapperTest {

    @Test
    public void shouldSerialisePojo() {
        final ITypeMapper mapper = new TypeMapper();

        assertThat(SERVICE_POLICY1,
                   equalTo(mapper.toPolicy(mapper.toPolicyAsString(SERVICE_POLICY1))));
        assertThat(APPLICATION_POLICY1,
                   equalTo(mapper.toPolicy(mapper.toPolicyAsString(APPLICATION_POLICY1))));

    }

    @Test
    public void shouldSerialisePojoSet() {
        final ITypeMapper mapper = new TypeMapper();

        assertThat(SERVICE_POLICY_SET,
                   containsInAnyOrder(mapper.toPolicies(mapper.toPoliciesAsString(SERVICE_POLICY_SET))
                                            .toArray()));
        assertThat(APP_POLICY_SET,
                   containsInAnyOrder(mapper.toPolicies(mapper.toPoliciesAsString(APP_POLICY_SET))
                                            .toArray()));
    }

    @Test
    public void shouldBeNullSafe() {
        final ITypeMapper mapper = new TypeMapper();

        assertThat(mapper.toPolicies(null), empty());

        assertThat(mapper.toPoliciesAsString(null), empty());
    }

    @Test(expected = PlatformFailedException.class)
    public void shouldThrowErrorOnDodgyJsonPolicy() {

        final ITypeMapper mapper = new TypeMapper();

        mapper.toPolicy("{abc}");
    }

    @Test(expected = PlatformFailedException.class)
    public void shouldThrowErrorOnDodgyJsonPolicies() {

        final ITypeMapper mapper = new TypeMapper();

        mapper.toPolicies(new HashSet<>(Arrays.asList("{abcd}")));
    }

}

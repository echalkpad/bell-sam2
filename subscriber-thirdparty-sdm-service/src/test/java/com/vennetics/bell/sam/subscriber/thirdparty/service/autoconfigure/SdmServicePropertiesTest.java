package com.vennetics.bell.sam.subscriber.thirdparty.service.autoconfigure;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class SdmServicePropertiesTest {

    @Rule
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public ExpectedException thrown = ExpectedException.none();

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() throws Exception {

        // MockEnvironment env = new MockEnvironment();
        // env.setProperty("sdm.service.validOus", "022,023,206,409,410,502");
        context = new AnnotationConfigApplicationContext();
        final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerScope("refresh", new SimpleThreadScope());
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,
                                                                  new String[] {
                                                                          "sdm.service.validOus=022,023,206,409,410,502" });
        // this.context.setEnvironment(env);
        context.register(SdmServiceProperties.class);
        context.refresh();
        assertThat(context.getBean(SdmServiceProperties.class).getValidOus().contains("023"),
                   is(true));
    }

}

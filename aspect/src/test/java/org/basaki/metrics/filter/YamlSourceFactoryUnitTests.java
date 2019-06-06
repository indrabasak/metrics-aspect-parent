package org.basaki.metrics.filter;

import java.io.IOException;
import java.util.Arrays;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * {@code YamlSourceFactoryUnitTests} is the unit test class for
 * {@code YamlSourceFactory}.
 * <p>
 *
 * @author Indra Basak
 * @since 06/05/19
 */
@RunWith(JUnitParamsRunner.class)
public class YamlSourceFactoryUnitTests {

    @Test
    @Parameters
    public void testCreatePropertySource(String source,
            EncodedResource resource, boolean expected) {
        YamlSourceFactory factory = new YamlSourceFactory();
        boolean found = false;
        PropertySource<?> propertySource = null;
        try {
            propertySource = factory.createPropertySource(source, resource);
            found = true;
        } catch (IOException e) {
            //do nothing
        }

        if (expected) {
            assertTrue(found);
            assertNotNull(propertySource);
        } else {
            assertFalse(found);
        }

    }

    public Iterable<Object[]> parametersForTestCreatePropertySource() {
        return Arrays.asList(new Object[][]{
                {"filterOne", new EncodedResource(
                        new ClassPathResource("/test-filter.yml")), true},
                {"filterOne", new EncodedResource(
                        new ClassPathResource("/dummy-filter.yml")), false},
        });
    }
}

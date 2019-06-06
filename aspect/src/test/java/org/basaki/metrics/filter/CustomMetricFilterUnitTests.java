package org.basaki.metrics.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.basaki.metrics.filter.MetricFilterProperties.MetricFilterProperty;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * {@code CustomMetricFilterUnitTests} is the unit test class for
 * {@code CustomMetricFilter}.
 *  <p>
 *
 * @author Indra Basak
 * @since 06/05/19
 */
@RunWith(JUnitParamsRunner.class)
public class CustomMetricFilterUnitTests {

    @Test
    @Parameters
    public void testMatches(
            Map<String, MetricFilterProperty> properties,
            int expectedCount, String... names) {
        CustomMetricFilter filter =
                new CustomMetricFilter(new MetricFilterProperties(properties));

        int count = 0;
        for (int i = 0; i < names.length; i++) {
            if (filter.matches(names[i], null)) {
                count++;
            }
        }

        assertEquals(expectedCount, count);

    }

    public Iterable<Object[]> parametersForTestMatches() {
        HashMap<String, MetricFilterProperty> propsOne
                = new HashMap<>();
        propsOne.put("metricOne", new MetricFilterProperty("timer"));
        propsOne.put("metricTwo", new MetricFilterProperty("meter"));

        return Arrays.asList(new Object[][]{
                {propsOne, 2, "metricOne", "metricTwo"},
                {propsOne, 1, "metricTwo", "metricThree"},
                {propsOne, 0, "metricThree", "metricFour"},
                {null, 2, "metricThree", "metricFour"},
        });
    }
}

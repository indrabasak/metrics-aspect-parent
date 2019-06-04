package org.basaki.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * {@code JmxMetricsConfigurationUnitTests} is the unit test class for
 * @code JmxMetricsConfiguration}.
 * <p>
 *
 * @author Indra Basak
 * @since 06/04/19
 */
public class JmxMetricsConfigurationUnitTests {

    @Test
    public void testRegisterJmxReporter() {
        JmxMetricsConfiguration config = new JmxMetricsConfiguration();

        final MetricRegistry registry = mock(MetricRegistry.class);
        JmxReporter reporter = config.registerJmxReporter(registry);
        assertNotNull(reporter);
    }
}

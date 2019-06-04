package org.basaki.metrics.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * {@code ServletMetricsConfigurationUnitTests} is the unit test class for
 *
 * @author Indra Basak
 * @code ServletMetricsConfiguration}.
 * <p>
 * @since 06/04/19
 */
public class ServletMetricsConfigurationUnitTests {

    @Test
    public void testRegisterHttpReporter() {
        ServletMetricsConfiguration config = new ServletMetricsConfiguration();
        config.setUrlMappings("/indra/metrics/");

        assertEquals("/indra/metrics/", config.getUrlMappings());
        assertNotNull(config.registerHttpReporter());
        assertNotNull(config.registerHttpReporter().getServletName());
    }
}

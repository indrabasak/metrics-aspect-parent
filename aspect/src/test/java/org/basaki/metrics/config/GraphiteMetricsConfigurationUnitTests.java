package org.basaki.metrics.config;

import com.codahale.metrics.MetricRegistry;
import java.net.UnknownHostException;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * {@code GraphiteMetricsConfigurationUnitTests} is the unit test class for
 * @code GraphiteMetricsConfiguration}.
 * <p>
 *
 * @author Indra Basak
 * @since 06/04/19
 */
public class GraphiteMetricsConfigurationUnitTests {

    @Test
    public void testRegisterGraphiteReporter() throws UnknownHostException {
        GraphiteMetricsConfiguration config =
                new GraphiteMetricsConfiguration();
        config.setHost("noname.company.com");
        config.setPort(2019);
        config.setGroup("testGroup");

        assertNotNull(config.getHost());
        assertNotNull(config.getPort());
        assertNotNull(config.getGroup());

        final MetricRegistry registry = mock(MetricRegistry.class);
        assertNotNull(config.registerGraphiteReporter(registry));
    }
}

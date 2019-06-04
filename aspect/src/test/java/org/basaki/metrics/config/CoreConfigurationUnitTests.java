package org.basaki.metrics.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@code CoreConfigurationUnitTests} is the unit test class for
 * @code CoreMetricsConfiguration}.
 * <p>
 *
 * @author Indra Basak
 * @since 06/04/19
 */
public class CoreConfigurationUnitTests {

    @Test
    public void testGetMetricRegistry() {
        CoreMetricsConfiguration config = new CoreMetricsConfiguration();
        assertNotNull(config.getMetricRegistry());
        assertNotNull(config.getMetricRegistry().getMetrics());
        assertFalse(config.getMetricRegistry().getMetrics().isEmpty());
    }

    @Test
    public void testGetContextListener() {
        CoreMetricsConfiguration config = new CoreMetricsConfiguration();

        ServletContextListener listener = config.getContextListener();
        assertNotNull(listener);

        final ServletContextEvent event = mock(ServletContextEvent.class);
        final ServletContext context = mock(ServletContext.class);
        when(event.getServletContext()).thenReturn(context);
        listener.contextInitialized(event);
        listener.contextDestroyed(event);
    }

    @Test
    public void testRegisterHealthCheckRegistry() {
        CoreMetricsConfiguration config = new CoreMetricsConfiguration();
        assertNotNull(config.registerHealthCheckRegistry());
        assertNotNull(config.registerHealthCheckRegistry().getListener());
    }
}

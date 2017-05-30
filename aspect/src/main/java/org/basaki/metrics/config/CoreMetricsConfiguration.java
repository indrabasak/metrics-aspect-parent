package org.basaki.metrics.config;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;

/**
 * {@code MetricConfiguration} is the configuration class for enabling
 * metrics. It registers JMX, Graphite, and HTTP reporters.
 * <p>
 *
 * @author Indra Basak
 * @since 10/17/16
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {
        "org.basaki.metrics.aspect"})
@Slf4j
public class CoreMetricsConfiguration {

    /**
     * Retrieves a metrics registry which acts as a cache for all
     * the metrics
     *
     * @return metric cache
     */
    @Primary
    @Bean(name = "registry")
    public MetricRegistry getMetricRegistry() {
        MetricRegistry registry = SharedMetricRegistries.getOrCreate("");
        registerMetric(registry, new GarbageCollectorMetricSet(), "jvm", "gc");
        registerMetric(registry, new MemoryUsageGaugeSet(), "jvm", "memory");
        registerMetric(registry, new ThreadStatesGaugeSet(), "jvm",
                "thread-states");
        registerMetric(registry, new FileDescriptorRatioGauge(), "jvm", "fd",
                "usage");

        return registry;
    }

    /**
     * Needed during test to avoid duplicate metric registration. Only happens
     * during testing.
     *
     * @param registry metric registry where all metrics are stored
     * @param name     first element of metric name
     * @param names    remaining elements of metric name
     * @param metric   a metric or set of metric to be registered
     */
    private void registerMetric(MetricRegistry registry, Metric metric,
            String name, String... names) {
        try {
            registry.register(MetricRegistry.name(name, names), metric);
        } catch (Exception e) {
            //don't do anything - only happens during test
        }
    }

    /**
     * Makes the metric registry available to the health check registry.
     *
     * @return a servlet listener
     */
    @Bean
    public ServletContextListener getContextListener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                log.debug("Registering Metrics Registry...");
                sce.getServletContext().setAttribute(
                        MetricsServlet.METRICS_REGISTRY,
                        getMetricRegistry());
            }

            @Override
            public void contextDestroyed(ServletContextEvent sc) {

            }
        };
    }

    /**
     * Registers a health check registry which is needed by
     * <tt>HealthCheckServlet</tt>. The health check servlet is instantiated by
     * the admin servlet.
     *
     * @return a servlet listener which has the Dropwizard's health check
     * registry. The registry contains all the metrics.
     */
    @Bean
    public ServletListenerRegistrationBean<HealthCheckServlet.ContextListener> registerHealthCheckRegistry() {
        final HealthCheckRegistry healthCheckRegistry =
                new HealthCheckRegistry();
        final HealthCheckServlet.ContextListener listener =
                new HealthCheckServlet.ContextListener() {
                    @Override
                    protected HealthCheckRegistry getHealthCheckRegistry() {
                        log.debug("Registering Health Check Registry...");
                        return healthCheckRegistry;
                    }
                };
        return new ServletListenerRegistrationBean<>(listener);
    }
}

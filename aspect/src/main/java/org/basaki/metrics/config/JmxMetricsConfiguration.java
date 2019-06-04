package org.basaki.metrics.config;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by indra.basak on 4/28/17.
 */
@Configuration
@ConditionalOnProperty(name = {"metrics.report.jmx"}, matchIfMissing = true)
@AutoConfigureAfter({CoreMetricsConfiguration.class})
@Slf4j
public class JmxMetricsConfiguration {

    /**
     * Registers a JMX reporter which listens for new metrics and exposes them
     * as namespaced MBeans.
     *
     * @param registry previously initialized metric registry
     * @return a JMX reporter
     */
    @Bean
    public JmxReporter registerJmxReporter(
            @Qualifier("registry") MetricRegistry registry) {
        final JmxReporter reporter =
                JmxReporter.forRegistry(registry).convertRatesTo(
                        TimeUnit.SECONDS).convertDurationsTo(
                        TimeUnit.MILLISECONDS)
                        .filter(MetricFilter.ALL)
                        .build();
        reporter.start();
        log.debug("Registered JMX metrics reporter.");

        return reporter;
    }
}

package org.basaki.metrics.config;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by indra.basak on 4/28/17.
 */
@Configuration
@ConditionalOnProperty(name = {"metrics.report.graphite"})
@AutoConfigureAfter({CoreMetricsConfiguration.class})
@Slf4j
public class GraphiteMetricsConfiguration {

    @Value("${graphite.host:localhost}")
    private String host;

    @Value("${graphite.port:2013}")
    private Integer port;

    @Value("${graphite.group:metrics}")
    private String group;
    /**
     * Registers a reporter which publishes metrics to a Carbon server by TCP.
     *
     * @param registry previously initialized metric registry
     * @return a Graphite reporter
     * @throws UnknownHostException if Carbon server is not found
     */
    @Bean
    public GraphiteReporter registerGraphiteReporter(
            @Qualifier("registry") MetricRegistry registry) throws UnknownHostException {
        final GraphiteSender graphite =
                new Graphite(new InetSocketAddress(host, port));

        String prefix = "servers." + hostname() + "." + group;
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith(prefix)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        reporter.start(1, TimeUnit.MINUTES);
        log.debug("Registered Graphite metrics reporter.");

        return reporter;
    }

    /**
     * Retrieves the host name of the local sever. It's used in Graphite
     * reporter.
     *
     * @return the host name
     * @throws UnknownHostException if local host name is not found
     */
    @Bean
    public String hostname() throws UnknownHostException {
        String hostName = InetAddress.getLocalHost().getHostName();
        int index = hostName.indexOf(".local");
        hostName = (index > 0) ? hostName.substring(0, index) : hostName;
        return hostName;
    }
}

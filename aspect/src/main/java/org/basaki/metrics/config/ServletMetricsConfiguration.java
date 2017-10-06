package org.basaki.metrics.config;

import com.codahale.metrics.servlets.AdminServlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by indra.basak on 4/28/17.
 */
@Configuration
@ConditionalOnProperty(name = {"metrics.report.http"})
@AutoConfigureAfter({CoreMetricsConfiguration.class})
@Slf4j
public class ServletMetricsConfiguration {

    @Value("${metrics.report.http.url:/metrics/*}")
    private String urlMappings;

    /**
     * Registers the admin servlet to make all the metrics available by HTTP at
     * the url http://&lt;host&gt;:&lt;port&gt;/metrics
     *
     * @return the admin servlet responsible for making the Dropwizard metrics
     * available
     */
    @Bean(name = "metricsServlet")
    public ServletRegistrationBean registerHttpReporter() {
        return new ServletRegistrationBean(new AdminServlet(), urlMappings) {
            @Override
            public String getServletName() {
                log.debug("Registering metrics admin servlet...");
                return "MetricsAdmin";
            }
        };
    }
}

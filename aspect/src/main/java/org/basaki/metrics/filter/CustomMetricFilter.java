package org.basaki.metrics.filter;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;

/**
 * {@code CustomMetricFilter} is a untility class for filtering metrics based on
 * a YAML configuration.
 * metrics.
 * <p>
 *
 * @author Indra Basak
 * @since 06/05/19
 */
public class CustomMetricFilter implements MetricFilter {
    private MetricFilterProperties properties;

    public CustomMetricFilter(MetricFilterProperties properties) {
        this.properties = properties;
    }

    /**
     * Filters a metric based on name.
     *
     * @param name   name of the metric
     * @param metric metric object
     * @return true if the metric matches the filter, false otherwise.
     */
    @Override
    public boolean matches(String name, Metric metric) {
        if (properties == null || properties.getProperties() == null) {
            return true;
        }

        if (properties.getProperties().containsKey(name)) {
            return true;
        }

        return false;
    }
}

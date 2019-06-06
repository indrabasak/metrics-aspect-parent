package org.basaki.metrics.filter;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * {@code MetricFilterProperties} is a set of properties used for filtering
 * metrics.
 * <p>
 *
 * @author Indra Basak
 * @since 06/05/19
 */
@PropertySource(factory = YamlSourceFactory.class,
        value = {"classpath:/config/${metrics.filter}",
                "classpath:/${metrics.filter}",
                "file:./config/${metrics.filter}",
                "file:./${metrics.filter}"}, ignoreResourceNotFound = true)
@ConfigurationProperties("filter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricFilterProperties {

    private Map<String, MetricFilterProperty> properties;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EnableConfigurationProperties
    public static class MetricFilterProperty {
        private String type;
    }
}

package org.basaki.metrics.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * {@code YamlSourceFactory} is a used reading properties from a YAML file.
 * metrics.
 * <p>
 *
 * @author Indra Basak
 * @since 06/05/19
 */
public class YamlSourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String source,
            EncodedResource resource) throws IOException {
        Properties props = readYaml(resource);
        String name =
                source != null ? source : resource.getResource().getFilename();

        return new PropertiesPropertySource(name, props);
    }

    private Properties readYaml(EncodedResource resource)
            throws FileNotFoundException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();

            return factory.getObject();
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                throw ((FileNotFoundException) e.getCause());
            }

            throw e;
        }
    }
}

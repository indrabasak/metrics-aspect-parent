package org.basaki.example.book.config;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.basaki.example.book.util.UuidBeanFactory;
import org.basaki.metrics.set.JmxGaugeSet;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by indra.basak on 3/8/17.
 */
@Configuration
public class SpringConfiguration {

    @Autowired
    private MetricRegistry registry;

    @Bean
    public static Mapper getMapper() {
        BeanMappingBuilder builder = new BeanMappingBuilder() {
            protected void configure() {
                mapping(UUID.class, UUID.class, TypeMappingOptions.oneWay(),
                        TypeMappingOptions.beanFactory(
                                UuidBeanFactory.class.getName()));
            }
        };

        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.addMapping(builder);

        return mapper;
    }

    @Primary
    @Bean
    public ObjectMapper createCustomObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JodaModule());

        return mapper;
    }

    @PostConstruct
    public void init() {
        registry.register(
                MetricRegistry.name("tomcat", "threadpool", "http-nio-8080"),
                new JmxGaugeSet("Tomcat:type=ThreadPool,name=\"http-nio-8080\""));
    }
}

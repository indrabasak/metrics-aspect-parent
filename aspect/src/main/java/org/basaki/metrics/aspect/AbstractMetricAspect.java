package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import java.lang.annotation.Annotation;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * {@code AbstractMetricAspect} is the abstract base class for all Metric
 * aspect.
 * <p>
 *
 * @param <A> Dropwizard metric annotation type
 * @author Indra Basak
 * @since 10/20/16
 */
@Getter
public abstract class AbstractMetricAspect<A extends Annotation> {
    private MetricRegistry registry;

    public AbstractMetricAspect(MetricRegistry registry) {
        this.registry = registry;
    }

    /**
     * Create a metric name based on the parameters provided.
     *
     * @param prefix     prefix added to the string name to make a distinction
     *                   between different metric types.
     * @param target     the  {@code java.lang.Class} where the annotation
     *                   is declared.
     * @param methodName the name of the method which is being currently
     *                   invoked
     * @param name       the name attribute specified inside a Dropwizard
     *                   annotation
     * @param absolute   if {@code true}, use the given name as an absolute
     *                   name
     *                   with the prefix prepended. If {@code false}, use the
     *                   given name relative to the annotated class.
     * @return the name of the metric
     */
    public String getMetricName(String prefix, Class target,
            String methodName, String name, boolean absolute) {
        String metricName;

        if (name != null && !name.trim().isEmpty()) {
            if (absolute) {
                metricName = prefix + name;
            } else {
                metricName = prefix + target.getCanonicalName() + "." + name;
            }
        } else {
            metricName =
                    prefix + target.getCanonicalName() + "." + methodName;
        }

        return metricName;
    }

    /**
     * Retrieves the annotation from the method if the abbotation is not
     * declared in the type (class, interface) definition.
     *
     * @param pointcut   an aspect pointcut
     * @param annotation a Dropwizard annotation declared at type level
     * @return a Dropwizard metric annotation
     */
    public abstract A getAnnotation(ProceedingJoinPoint pointcut,
            A annotation);
}

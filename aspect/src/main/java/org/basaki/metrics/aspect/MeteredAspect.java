package org.basaki.metrics.aspect;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Metered;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@code MeteredAspect} intercepts any method execution if a class or
 * method is tagged with <code>com.codahale.metrics.annotation.Metered</code>
 * annotation.
 * <p>
 *
 * @author Indra Basak
 * @since 10/18/16
 */
@Aspect
@Component
public class MeteredAspect extends AbstractMetricAspect<Metered> {

    public static final String METRIC_PREFIX = "meter.";

    @Autowired
    public MeteredAspect(MetricRegistry registry) {
        super(registry);
    }

    /**
     * Generates meter metric of type {@code com.codahale.metrics.Meter}.
     * It increases the invocation count when a method is executed.
     *
     * @param pointcut   aspect point cut
     * @param annotation the {@code Metered} annotation declared at the
     *                   class level. It will be null if it's declared at
     *                   method
     *                   level
     * @return the return value from the invoked target object's method
     * @throws Throwable any exception encountered during target method
     *                   execution
     */
    @Around("@annotation(annotation) @within(annotation)")
    public Object generateMetric(
            final ProceedingJoinPoint pointcut,
            final Metered annotation) throws Throwable {
        Metered anno = getAnnotation(pointcut, annotation);

        String metricName = getMetricName(METRIC_PREFIX,
                pointcut.getSignature().getDeclaringType(),
                pointcut.getSignature().getName(), anno.name(),
                anno.absolute());
        final Meter meter = getRegistry().meter(metricName);

        meter.mark();
        return pointcut.proceed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Metered getAnnotation(final ProceedingJoinPoint pointcut,
            final Metered annotation) {
        if (annotation != null) {
            return annotation;
        }

        Metered retAnnotation = null;
        if (pointcut.getSignature() instanceof MethodSignature) {
            MethodSignature signature =
                    (MethodSignature) pointcut.getSignature();
            Method method = signature.getMethod();
            retAnnotation = method.getAnnotation(Metered.class);
        }

        return retAnnotation;
    }
}

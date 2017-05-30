package org.basaki.metrics.aspect;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.ExceptionMetered;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@code ExceptionMeteredAspect} intercepts any method execution if a class
 * or
 * method is tagged with {@code com.codahale.metrics.annotation.ExceptionMeter}
 * annotation.
 * <p>
 *
 * @author Indra Basak
 * @since 10/19/16
 */
@Component
@Aspect
public class ExceptionMeteredAspect extends AbstractMetricAspect<ExceptionMetered> {

    public static final String METRIC_PREFIX = "exception.";

    @Autowired
    public ExceptionMeteredAspect(MetricRegistry registry) {
        super(registry);
    }

    /**
     * Generates exception metric of type {@code com.codahale.metrics.Meter}
     * if an exception is invoked while executing the target method.
     *
     * @param pointcut   aspect point cut
     * @param annotation the {@code ExceptionMetered} annotation declared at the
     *                   class level. It will be null if it's declared at method
     *                   level
     * @return thge return value from the invoked target object's method
     * @throws Throwable any exception encountered during target method
     *                   execution
     */
    @Around("@annotation(annotation) || @within(annotation)")
    public Object generateMetric(ProceedingJoinPoint pointcut,
            ExceptionMetered annotation) throws Throwable {
        ExceptionMetered anno = getAnnotation(pointcut, annotation);
        String metricName = getMetricName(METRIC_PREFIX,
                pointcut.getSignature().getDeclaringType(),
                pointcut.getSignature().getName(), anno.name(),
                anno.absolute());
        final Meter meter = getRegistry().meter(metricName);

        try {
            return pointcut.proceed();
        } catch (Throwable t) {
            if (anno.cause().isAssignableFrom(t.getClass())) {
                meter.mark();
            }
            throw t;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExceptionMetered getAnnotation(
            ProceedingJoinPoint pointcut,
            ExceptionMetered annotation) {
        if (annotation != null) {
            return annotation;
        }

        ExceptionMetered retAnnotation = null;
        if (pointcut.getSignature() instanceof MethodSignature) {
            MethodSignature signature =
                    (MethodSignature) pointcut.getSignature();
            Method method = signature.getMethod();
            retAnnotation = method.getAnnotation(ExceptionMetered.class);
        }

        return retAnnotation;
    }
}

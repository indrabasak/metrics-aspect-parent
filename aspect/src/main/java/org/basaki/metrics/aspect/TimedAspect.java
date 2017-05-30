package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@code TimedAspect} intercepts any method execution if a class or
 * method is tagged with {@code com.codahale.metrics.annotation.Timed}
 * annotation.
 * <p>
 *
 * @author Indra Basak
 * @since 10/17/16
 */
@Aspect
@Component
public class TimedAspect extends AbstractMetricAspect<Timed> {

    public static final String METRIC_PREFIX = "timer.";

    @Autowired
    public TimedAspect(MetricRegistry registry) {
        super(registry);
    }

    /**
     * Generates timer metric of type {@code com.codahale.metrics.Timer}.
     * It measures the time it take to invoke a method.
     *
     * @param pointcut   aspect point cut
     * @param annotation the {@code Timed} annotation declared at the class
     *                   level. It will be null if it's declared at method
     *                   level
     * @return the return value from the invoked target object's method
     * @throws Throwable any exception encountered during target method
     *                   execution
     */
    @Around("@annotation(annotation) || @within(annotation)")
    public Object generateMetric(
            final ProceedingJoinPoint pointcut,
            final Timed annotation) throws Throwable {
        Timed anno = getAnnotation(pointcut, annotation);
        String metricName = getMetricName(METRIC_PREFIX,
                pointcut.getSignature().getDeclaringType(),
                pointcut.getSignature().getName(), anno.name(),
                anno.absolute());
        final Timer timer = getRegistry().timer(metricName);
        final Timer.Context context = timer.time();

        try {
            return pointcut.proceed();
        } finally {
            context.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timed getAnnotation(final ProceedingJoinPoint pointcut,
            final Timed annotation) {
        if (annotation != null) {
            return annotation;
        }

        Timed retAnnotation = null;
        if (pointcut.getSignature() instanceof MethodSignature) {
            MethodSignature signature =
                    (MethodSignature) pointcut.getSignature();
            Method method = signature.getMethod();
            retAnnotation = method.getAnnotation(Timed.class);
        }

        return retAnnotation;
    }
}

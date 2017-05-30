package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.ExceptionMetered;
import java.lang.annotation.Annotation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * {@code ExceptionMeteredAspectUnitTests} is the unit test class for @code
 * ExceptionMeteredAspect}.
 * <p>
 *
 * @author Indra Basak
 * @since 4/26/17
 */
public class ExceptionMeteredAspectUnitTests {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Signature signature;

    @Mock
    private ProceedingJoinPoint pointcut;

    private MetricRegistry registry;

    private ExceptionMeteredAspect aspect;

    private Class clazz = String.class;

    private String methodName = "length";

    private String customName = "testExceptionMetric";

    @Before
    public void setUp() {
        registry = new MetricRegistry();
        aspect = new ExceptionMeteredAspect(registry);
    }

    public void test(ExceptionMetered annotation,
            String metricName) throws Throwable {
        when(signature.getDeclaringType()).thenReturn(clazz);
        when(signature.getName()).thenReturn(methodName);
        when(pointcut.getSignature()).thenReturn(signature);
        when(pointcut.proceed()).thenThrow(new IllegalArgumentException());

        try {
            aspect.generateMetric(pointcut, annotation);
        } catch (Exception e) {
            //do nothing
            //exception expected
        }

        assertEquals(metricName, registry.getMeters().firstKey());
        assertEquals(1, registry.meter(metricName).getCount());
        assertTrue("Metric mean rate is not greater than zero",
                (registry.meter(metricName).getMeanRate() > 0));
    }

    @Test
    public void testGenerateMetric() throws Throwable {
        ExceptionMetered annotation =
                ExceptionMeteredAnnotation.getInstance(null, false,
                        IllegalArgumentException.class);
        String metricName =
                ExceptionMeteredAspect.METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName;
        test(annotation, metricName);
    }

    @Test
    public void testGenerateMetricWithNameAndAbsoluteFalse() throws Throwable {
        ExceptionMetered annotation =
                ExceptionMeteredAnnotation.getInstance(customName, false,
                        IllegalArgumentException.class);
        String metricName =
                ExceptionMeteredAspect.METRIC_PREFIX + clazz.getCanonicalName() + "." + customName;
        test(annotation, metricName);
    }

    @Test
    public void testGenerateMetricWithNameAndAbsoluteTrue() throws Throwable {
        ExceptionMetered annotation =
                ExceptionMeteredAnnotation.getInstance(customName, true,
                        IllegalArgumentException.class);
        String metricName =
                ExceptionMeteredAspect.METRIC_PREFIX + customName;
        test(annotation, metricName);
    }

    public static class ExceptionMeteredAnnotation {
        public static ExceptionMetered getInstance(final String name,
                final boolean absolute,
                final Class<? extends Throwable> clazz) {
            ExceptionMetered instance = new ExceptionMetered() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String name() {
                    return name;
                }

                @Override
                public boolean absolute() {
                    return absolute;
                }

                @Override
                public Class<? extends Throwable> cause() {
                    return clazz;
                }
            };

            return instance;
        }
    }
}

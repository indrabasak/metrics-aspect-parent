package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
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
 * {@code TimedAspectUnitTests} is the unit test class for @code TimedAspect}.
 * <p>
 *
 * @author Indra Basak
 * @since 4/26/17
 */
public class TimedAspectUnitTests {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Signature signature;

    @Mock
    private ProceedingJoinPoint pointcut;

    private MetricRegistry registry;

    private TimedAspect aspect;

    private Class clazz = String.class;

    private String methodName = "length";

    private String customName = "testMetric";

    @Before
    public void setUp() {
        registry = new MetricRegistry();
        aspect = new TimedAspect(registry);
    }

    public void test(Timed annotation, String metricName) throws Throwable {
        when(signature.getDeclaringType()).thenReturn(clazz);
        when(signature.getName()).thenReturn(methodName);
        when(pointcut.getSignature()).thenReturn(signature);

        aspect.generateMetric(pointcut, annotation);
        assertEquals(metricName, registry.getTimers().firstKey());
        assertEquals(1, registry.timer(metricName).getCount());
        assertTrue("Metric mean rate is not greater than zero",
                (registry.timer(metricName).getMeanRate() > 0));
    }

    @Test
    public void testGenerateMetric() throws Throwable {
        Timed annotation = TimedAnnotation.getInstance(null, false);
        String metricName =
                TimedAspect.METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName;
        test(annotation, metricName);
    }

    @Test
    public void testGenerateMetricWithNameAndAbsoluteFalse() throws Throwable {
        Timed annotation = TimedAnnotation.getInstance(customName, false);
        String metricName =
                TimedAspect.METRIC_PREFIX + clazz.getCanonicalName() + "." + customName;
        test(annotation, metricName);
    }

    @Test
    public void testGenerateMetricWithNameAndAbsoluteTrue() throws Throwable {
        Timed annotation = TimedAnnotation.getInstance(customName, true);
        String metricName =
                TimedAspect.METRIC_PREFIX + customName;
        test(annotation, metricName);
    }

    public static class TimedAnnotation {
        public static Timed getInstance(final String name,
                final boolean absolute) {
            Timed instance = new Timed() {

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
            };

            return instance;
        }
    }
}

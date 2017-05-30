package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Metered;
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
 * {@code MeteredAspectUnitTests} is the unit test class for @code
 * MeteredAspect}.
 * <p>
 *
 * @author Indra Basak
 * @since 4/26/17
 */
public class MeteredAspectUnitTests {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Signature signature;

    @Mock
    private ProceedingJoinPoint pointcut;

    private MetricRegistry registry;

    private MeteredAspect aspect;

    private Class clazz = String.class;

    private String methodName = "length";

    private String customName = "testMetric";

    @Before
    public void setUp() {
        registry = new MetricRegistry();
        aspect = new MeteredAspect(registry);
    }


    private void test(Metered annotation, String metricName) throws Throwable {
        when(signature.getDeclaringType()).thenReturn(clazz);
        when(signature.getName()).thenReturn(methodName);
        when(pointcut.getSignature()).thenReturn(signature);

        aspect.generateMetric(pointcut, annotation);
        assertEquals(metricName, registry.getMeters().firstKey());
        assertEquals(1, registry.meter(metricName).getCount());
        assertTrue("Metric mean rate is not greater than zero",
                (registry.meter(metricName).getMeanRate() > 0));
    }

    @Test
    public void testGenerateMetric() throws Throwable {
        Metered annotation = MeteredAnnotation.getInstance(null, false);
        String metricName =
                MeteredAspect.METRIC_PREFIX + clazz.getCanonicalName()
                        + "." + methodName;
        test(annotation, metricName);
    }


    @Test
    public void testGenerateMetricWithNameAndAbsoluteFalse() throws Throwable {
        Metered annotation = MeteredAnnotation.getInstance(customName, false);
        String metricName =
                MeteredAspect.METRIC_PREFIX + clazz.getCanonicalName()
                        + "." + customName;
        test(annotation, metricName);
    }

    @Test
    public void testGenerateMetricWithNameAndAbsoluteTrue() throws Throwable {
        Metered annotation = MeteredAnnotation.getInstance(customName, true);
        String metricName =
                MeteredAspect.METRIC_PREFIX + customName;
        test(annotation, metricName);
    }

    public static class MeteredAnnotation {
        public static Metered getInstance(final String name,
                final boolean absolute) {
            Metered instance = new Metered() {

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

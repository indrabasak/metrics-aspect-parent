package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Metered;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.basaki.metrics.aspect.MeteredAspect.METRIC_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@code MeteredAspectUnitTests} is the unit test class for @code
 * MeteredAspect}.
 * <p>
 *
 * @author Indra Basak
 * @since 4/26/17
 */
@RunWith(JUnitParamsRunner.class)
public class MeteredAspectUnitTests {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ProceedingJoinPoint pointcut;

    private MetricRegistry registry;

    private MeteredAspect aspect;

    private Class clazz = String.class;

    private String methodName = "length";

    @Before
    public void setUp() {
        registry = new MetricRegistry();
        aspect = new MeteredAspect(registry);
    }

    @Test
    @Parameters
    public void testGenerateMetric(Metered annotation, Metered methodAnnotation,
            String metricName) throws Throwable {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getDeclaringType()).thenReturn(clazz);
        when(signature.getName()).thenReturn(methodName);
        when(pointcut.getSignature()).thenReturn(signature);

        Method method = mock(Method.class);
        when(method.getAnnotation(any())).thenReturn(methodAnnotation);
        when(signature.getMethod()).thenReturn(method);
        when(pointcut.getSignature()).thenReturn(signature);

        aspect.generateMetric(pointcut, annotation);
        assertEquals(metricName, registry.getMeters().firstKey());
        assertEquals(1, registry.meter(metricName).getCount());
        assertTrue("Metric mean rate is not greater than zero",
                (registry.meter(metricName).getMeanRate() > 0));
    }

    public Iterable<Object[]> parametersForTestGenerateMetric() {
        String customName = "testMetric";

        return Arrays.asList(new Object[][]{
                {MeteredAnnotation.getInstance(null, false), null,
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName},
                {null, MeteredAnnotation.getInstance(null, false),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName},

                {MeteredAnnotation.getInstance(customName, false), null,
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + customName},
                {null, MeteredAnnotation.getInstance(customName, false),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + customName},

                {MeteredAnnotation.getInstance(customName, true), null,
                        METRIC_PREFIX + customName},
                {null, MeteredAnnotation.getInstance(customName, true),
                        METRIC_PREFIX + customName},
        });
    }

    public static class MeteredAnnotation {
        static Metered getInstance(final String name,
                final boolean absolute) {
            return new Metered() {

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
        }
    }
}

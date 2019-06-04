package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
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

import static org.basaki.metrics.aspect.TimedAspect.METRIC_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@code TimedAspectUnitTests} is the unit test class for @code TimedAspect}.
 * <p>
 *
 * @author Indra Basak
 * @since 4/26/17
 */
@RunWith(JUnitParamsRunner.class)
public class TimedAspectUnitTests {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ProceedingJoinPoint pointcut;

    private MetricRegistry registry;

    private TimedAspect aspect;

    private Class clazz = String.class;

    private String methodName = "length";

    @Before
    public void setUp() {
        registry = new MetricRegistry();
        aspect = new TimedAspect(registry);
    }

    @Test
    @Parameters
    public void testGenerateMetric(Timed annotation, Timed methodAnnotation,
            String metricName) throws Throwable {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getDeclaringType()).thenReturn(clazz);
        when(signature.getName()).thenReturn(methodName);

        Method method = mock(Method.class);
        when(method.getAnnotation(any())).thenReturn(methodAnnotation);
        when(signature.getMethod()).thenReturn(method);
        when(pointcut.getSignature()).thenReturn(signature);

        aspect.generateMetric(pointcut, annotation);

        assertEquals(metricName, registry.getTimers().firstKey());
        assertEquals(1, registry.timer(metricName).getCount());
        assertTrue("Metric mean rate is not greater than zero",
                (registry.timer(metricName).getMeanRate() > 0));
    }

    public Iterable<Object[]> parametersForTestGenerateMetric() {
        String customName = "testMetric";

        return Arrays.asList(new Object[][]{
                {TimedAnnotation.getInstance(null, false), null,
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName},
                {null, TimedAnnotation.getInstance(null, false),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName},

                {TimedAnnotation.getInstance(customName, false), null,
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + customName},
                {null, TimedAnnotation.getInstance(customName, false),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + customName},

                {TimedAnnotation.getInstance(customName, true), null,
                        METRIC_PREFIX + customName},
                {null, TimedAnnotation.getInstance(customName, true),
                        METRIC_PREFIX + customName},
        });
    }

    public static class TimedAnnotation {
        static Timed getInstance(final String name,
                final boolean absolute) {
            return new Timed() {

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

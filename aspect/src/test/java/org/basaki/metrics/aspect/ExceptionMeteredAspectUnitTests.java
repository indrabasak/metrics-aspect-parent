package org.basaki.metrics.aspect;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.ExceptionMetered;
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

import static org.basaki.metrics.aspect.ExceptionMeteredAspect.METRIC_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@code ExceptionMeteredAspectUnitTests} is the unit test class for @code
 * ExceptionMeteredAspect}.
 * <p>
 *
 * @author Indra Basak
 * @since 4/26/17
 */
@RunWith(JUnitParamsRunner.class)
public class ExceptionMeteredAspectUnitTests {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ProceedingJoinPoint pointcut;

    private MetricRegistry registry;

    private ExceptionMeteredAspect aspect;

    private Class clazz = String.class;

    private String methodName = "length";

    @Before
    public void setUp() {
        registry = new MetricRegistry();
        aspect = new ExceptionMeteredAspect(registry);
    }

    @Test
    @Parameters
    public void testGenerateMetric(ExceptionMetered annotation,
            ExceptionMetered methodAnnotation,
            Throwable exception,
            String metricName) throws Throwable {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getDeclaringType()).thenReturn(clazz);
        when(signature.getName()).thenReturn(methodName);
        when(pointcut.getSignature()).thenReturn(signature);
        when(pointcut.proceed()).thenThrow(exception);

        Method method = mock(Method.class);
        when(method.getAnnotation(any())).thenReturn(methodAnnotation);
        when(signature.getMethod()).thenReturn(method);
        when(pointcut.getSignature()).thenReturn(signature);

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

    public Iterable<Object[]> parametersForTestGenerateMetric() {
        String customName = "testExceptionMetric";

        return Arrays.asList(new Object[][]{
                {ExceptionMeteredAnnotation.getInstance(null, false,
                        IllegalArgumentException.class), null,
                        new IllegalArgumentException(),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName},
                {null, ExceptionMeteredAnnotation.getInstance(null, false,
                        IllegalArgumentException.class),
                        new IllegalArgumentException(),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + methodName},

                {ExceptionMeteredAnnotation.getInstance(customName, false,
                        NullPointerException.class), null,
                        new NullPointerException(),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + customName},
                {null, ExceptionMeteredAnnotation.getInstance(customName, false,
                        NullPointerException.class),
                        new NullPointerException(),
                        METRIC_PREFIX + clazz.getCanonicalName() + "." + customName},

                {ExceptionMeteredAnnotation.getInstance(customName, true,
                        IllegalArgumentException.class), null,
                        new IllegalArgumentException(),
                        METRIC_PREFIX + customName},
                {null, ExceptionMeteredAnnotation.getInstance(customName, true,
                        IllegalArgumentException.class),
                        new IllegalArgumentException(),
                        METRIC_PREFIX + customName},
        });
    }

    public static class ExceptionMeteredAnnotation {
        static ExceptionMetered getInstance(final String name,
                final boolean absolute,
                final Class<? extends Throwable> clazz) {
            return new ExceptionMetered() {

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
        }
    }
}

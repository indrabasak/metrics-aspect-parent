package org.basaki.metrics.set;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Matchers.any;

/**
 * {@code JmxGaugeSetUnitTests} is the unit test class for {@code JmxGaugeSet}.
 *
 * @author Indra Basak
 * @code JmxGaugeSet}.
 * <p>
 * @since 06/04/19
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JmxGaugeSet.class, ManagementFactory.class})
@PowerMockIgnore({"javax.management.*"})
@PowerMockRunnerDelegate(JUnitParamsRunner.class)
public class JmxGaugeSetUnitTests {

    @Test
    @Parameters
    public void testGetMetrics(String name, Object value,
            boolean expectedEmpty) throws Exception {
        MBeanServer mBeanServer = Mockito.mock(MBeanServer.class);
        PowerMockito.mockStatic(ManagementFactory.class);
        Mockito.when(ManagementFactory.getPlatformMBeanServer())
                .thenReturn(mBeanServer);

        ObjectName objName = Mockito.mock(ObjectName.class);
        PowerMockito.whenNew(ObjectName.class)
                .withAnyArguments()
                .thenReturn(objName);

        MBeanInfo mBeanInfo = Mockito.mock(MBeanInfo.class);
        Mockito.when(mBeanServer.getMBeanInfo(any())).thenReturn(mBeanInfo);

        MBeanAttributeInfo info = new MBeanAttributeInfo("hello",
                "type", "description", true, true, false);
        MBeanAttributeInfo[] infos = {info};
        Mockito.when(mBeanInfo.getAttributes()).thenReturn(infos);
        Mockito.when(mBeanServer.getAttribute(any(), any())).thenReturn(value);

        JmxGaugeSet metricSet = new JmxGaugeSet(name);
        Map<String, Metric> gauges = metricSet.getMetrics();
        assertNotNull(gauges);
        if (expectedEmpty) {
            assertEquals(0, gauges.size());
        } else {
            assertEquals(1, gauges.size());
            if (value instanceof Number || value instanceof Boolean) {
                assertEquals(value,
                        ((Gauge<?>) gauges.get("hello")).getValue());
            } else {
                assertEquals(value.toString(),
                        ((Gauge<?>) gauges.get("hello")).getValue());
            }

        }
    }

    public Iterable<Object[]> parametersForTestGetMetrics() {
        return Arrays.asList(new Object[][]{
                {"a.b.c:type=nothing,id=0", 5, false},
                {"a.b.c:type=nothing,id=1", 5.6, false},
                {"a.b.c:type=nothing,id=2", true, false},
                {"a.b.c:type=nothing,id=3", "bye", false},
                {"a.b.c:type=nothing,id=4", new StringBuffer("hello"), false},
                {"a.b.c:type=nothing,id=5", 6L, false},
                {"anyname", 4, true},
        });
    }
}

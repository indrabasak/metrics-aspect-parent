package org.basaki.metrics.set;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code JmxGaugeSet} extracts metrics from a given JMX MBean.
 * <p>
 *
 * @author Indra Basak
 * @since 06/05/19
 */
@Slf4j
public class JmxGaugeSet implements MetricSet {

    private String name;

    /**
     * Constructs a {@code JmxGaugeSet} object.
     *
     * @param name a string representation of the JMX object name
     */
    public JmxGaugeSet(String name) {
        this.name = name;
    }

    /**
     * Retrieves a map of metric names to metrics.
     *
     * @return a map of metrics
     */
    @Override
    public Map<String, Metric> getMetrics() {
        Map<String, Metric> gauges = new HashMap<>();

        try {
            MBeanServer mBeanServer =
                    ManagementFactory.getPlatformMBeanServer();
            ObjectName objName = ObjectName.getInstance(name);
            MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objName);

            for (MBeanAttributeInfo info : mBeanInfo.getAttributes()) {
                String attrName = info.getName();
                Object value = mBeanServer.getAttribute(objName, attrName);

                if (value instanceof Number) {
                    gauges.put(attrName, (Gauge<Number>) () ->
                            new AttributeValue<Number>(mBeanServer, objName,
                                    attrName, 0, false).getValue());
                } else if (value instanceof Boolean) {
                    gauges.put(attrName, (Gauge<Boolean>) () ->
                            new AttributeValue<Boolean>(mBeanServer, objName,
                                    attrName, false, false).getValue());
                } else {
                    gauges.put(attrName, (Gauge<String>) () ->
                            new AttributeValue<String>(mBeanServer, objName,
                                    attrName, null, true).getValue());
                }
            }
        } catch (Exception e) {
            log.debug("Encountered error while fetching MBean.");
        }

        return Collections.unmodifiableMap(gauges);
    }

    private static class AttributeValue<T> {

        private boolean strg;

        private MBeanServer mBeanServer;

        private ObjectName objName;

        private String name;

        private Object defaultValue;

        AttributeValue(final MBeanServer mBeanServer, ObjectName objName,
                String name, Object defaultValue, boolean strg) {
            this.mBeanServer = mBeanServer;
            this.objName = objName;
            this.name = name;
            this.defaultValue = defaultValue;
            this.strg = strg;
        }

        public T getValue() {
            try {
                Object value = mBeanServer.getAttribute(objName, name);
                if (strg) {
                    return (T) value.toString();
                }

                return (T) value;
            } catch (Exception e) {
                return (T) defaultValue;
            }
        }
    }
}

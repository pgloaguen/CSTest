package com.pgloaguen.csprotocolexercise;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * Created by polo on 28/08/2016.
 */
public class MetricSerializerTest extends TestCase {

    private MetricSerializer metricSerializer = new MetricSerializer();
    private MetricFactory metricFactory = new MetricFactory();


    public void testHeaderSerializationCompletelyDifferent() {
        Metric m = new Metric(1, 2, 3.0, 4, "android".getBytes(), (short) 5);
        Metric m2 = new Metric(2, 3, 4.0, 5, "iOS".getBytes(), (short) 6);

        byte[] serialized = metricSerializer.serializedMetric(m2, m);
        byte header = serialized[4];

        assertEquals(MetricSerializer.SCREENRES_CHANGED, header & MetricSerializer.SCREENRES_CHANGED);
        assertEquals(MetricSerializer.OSNAME_CHANGED, header & MetricSerializer.OSNAME_CHANGED);
        assertEquals(MetricSerializer.CPU_CHANGED, header & MetricSerializer.CPU_CHANGED);
        assertEquals(MetricSerializer.ID_CHANGED, header & MetricSerializer.ID_CHANGED);
        assertEquals(MetricSerializer.FACTOR_CHANGED, header & MetricSerializer.FACTOR_CHANGED);
    }

    public void testHeaderSerializationCompletelyEquals() {
        Metric m = new Metric(1, 2, 3.0, 4, "android".getBytes(), (short) 5);
        Metric m2 = new Metric(1, 2, 3.0, 4, "android".getBytes(), (short) 5);

        byte[] serialized = metricSerializer.serializedMetric(m2, m);
        byte header = serialized[4];

        assertEquals(0, header & MetricSerializer.SCREENRES_CHANGED);
        assertEquals(0, header & MetricSerializer.OSNAME_CHANGED);
        assertEquals(0, header & MetricSerializer.CPU_CHANGED);
        assertEquals(0, header & MetricSerializer.ID_CHANGED);
        assertEquals(0, header & MetricSerializer.FACTOR_CHANGED);
    }

    public void testSerializationUnSerialization() {
        Metric m = metricFactory.nextMetric();
        Metric m2 = metricFactory.nextMetric();

        byte[] mByte = metricSerializer.serializedMetric(m, null);
        byte[] mByte2 = metricSerializer.serializedMetric(m2, m);


        ByteArrayInputStream ins = new ByteArrayInputStream(ByteBuffer.allocate(mByte.length+mByte2.length).put(mByte).put(mByte2).array());

        Metric unserializedMetric = metricSerializer.nextMetric(ins, null);
        assertEquals(m, unserializedMetric);

        Metric unserializedMetric2 = metricSerializer.nextMetric(ins, unserializedMetric);
        assertEquals(m2, unserializedMetric2);
    }
}

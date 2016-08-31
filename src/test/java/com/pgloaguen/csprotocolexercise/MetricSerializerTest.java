package com.pgloaguen.csprotocolexercise;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by polo on 28/08/2016.
 */
public class MetricSerializerTest extends TestCase {

    private MetricSerializer metricSerializer;
    private MetricFactory metricFactory;
    private ByteArrayOutputStream outputStream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        metricSerializer = new MetricSerializer();
        metricFactory = new MetricFactory();
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    protected void tearDown() throws Exception {
        outputStream.close();
        super.tearDown();
    }

    public void testHeaderSerializationCompletelyDifferent() {
        Metric m = new Metric(1, 2, 3.0, 4, "android".getBytes(), (short) 5);
        Metric m2 = new Metric(2, 3, 4.0, 5, "iOS".getBytes(), (short) 6);

        assertTrue(metricSerializer.serializedMetric(outputStream, m2, m));
        byte header = outputStream.toByteArray()[4];

        assertEquals(MetricSerializer.SCREENRES_CHANGED, header & MetricSerializer.SCREENRES_CHANGED);
        assertEquals(MetricSerializer.OSNAME_CHANGED, header & MetricSerializer.OSNAME_CHANGED);
        assertEquals(MetricSerializer.CPU_CHANGED, header & MetricSerializer.CPU_CHANGED);
        assertEquals(MetricSerializer.ID_CHANGED, header & MetricSerializer.ID_CHANGED);
        assertEquals(MetricSerializer.FACTOR_CHANGED, header & MetricSerializer.FACTOR_CHANGED);
    }

    public void testHeaderSerializationCompletelyEquals() {
        Metric m = new Metric(1, 2, 3.0, 4, "android".getBytes(), (short) 5);
        Metric m2 = new Metric(1, 2, 3.0, 4, "android".getBytes(), (short) 5);

        assertTrue(metricSerializer.serializedMetric(outputStream, m2, m));
        byte header = outputStream.toByteArray()[4];

        assertEquals(0, header & MetricSerializer.SCREENRES_CHANGED);
        assertEquals(0, header & MetricSerializer.OSNAME_CHANGED);
        assertEquals(0, header & MetricSerializer.CPU_CHANGED);
        assertEquals(0, header & MetricSerializer.ID_CHANGED);
        assertEquals(0, header & MetricSerializer.FACTOR_CHANGED);
    }

    public void testSerializationUnSerialization() {
        Metric m = metricFactory.nextMetric();
        Metric m2 = metricFactory.nextMetric();

        assertTrue(metricSerializer.serializedMetric(outputStream, m, null));
        assertTrue(metricSerializer.serializedMetric(outputStream, m2, m));


        ByteArrayInputStream ins = new ByteArrayInputStream(outputStream.toByteArray());

        Metric unserializedMetric = metricSerializer.nextMetric(ins, null);
        assertEquals(m, unserializedMetric);

        Metric unserializedMetric2 = metricSerializer.nextMetric(ins, unserializedMetric);
        assertEquals(m2, unserializedMetric2);
    }
}

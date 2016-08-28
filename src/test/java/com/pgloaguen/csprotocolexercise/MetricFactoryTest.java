package com.pgloaguen.csprotocolexercise;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * Created by polo on 27/08/2016.
 */
public class MetricFactoryTest extends TestCase {

    public void testIdIncrementation() {
        MetricFactory metricFactory = new MetricFactory();
        assertEquals(metricFactory.nextMetric().id + 1, metricFactory.nextMetric().id);
    }

}

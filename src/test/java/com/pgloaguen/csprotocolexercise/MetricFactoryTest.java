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

    public void testFactorial() {
        MetricFactory metricFactory = new MetricFactory();
        for (int i = 0; i < 7; i++) {
            metricFactory.nextMetric();
        }

        assertEquals(5, metricFactory.nextMetric().factorial);
    }

}

package com.pgloaguen.csprotocolexercise;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Serializes/Unserializes Metric object.
 * A custom binary format is used and only the updated fields are sent.
 */
public class MetricSerializer {

    public static final byte SCREENRES_CHANGED = 1;
    public static final byte OSNAME_CHANGED = 2;
    public static final byte CPU_CHANGED = 4;
    public static final byte ID_CHANGED = 8;
    public static final byte FACTOR_CHANGED = 16;
    public static final byte PID_CHANGED = 32;

    private static byte[] sIntByte = new byte[4];

    public int maxMetricSize(Metric m) {
        return 4 + 5 + 4 + 4 + m.osName.length + 8 + 4 + 2;
    }

    /**
     *
     * Create a new metric serialized and don't add value which are already been send in the last metric object for optimisation purpose.
     *
     * @param metric : the metric to serialized
     * @param lastMetricSent : the metric used to know which fields to send or not
     * @return the serialized metric
     */
    public byte[] serializedMetric(Metric metric, Metric lastMetricSent) {
        int bufferSize = 1; // 1 for the header
        byte header = 0;

        // Compute the size of the buffer needed and create the header
        if (lastMetricSent == null || lastMetricSent.pid != metric.pid) {
            header += PID_CHANGED;
            bufferSize += 4;
        }

        if (lastMetricSent == null || lastMetricSent.screenRes != metric.screenRes) {
            header += SCREENRES_CHANGED;
            bufferSize += 4;
        }

        if (lastMetricSent == null || !Arrays.equals(lastMetricSent.osName, metric.osName)) {
            header += OSNAME_CHANGED;
            bufferSize += 4 + metric.osName.length;
        }

        if (lastMetricSent == null || lastMetricSent.cpu != metric.cpu) {
            header += CPU_CHANGED;
            bufferSize += 8;
        }

        if (lastMetricSent == null || lastMetricSent.id != metric.id) {
            header += ID_CHANGED;
            bufferSize += 4;
        }

        if (lastMetricSent == null || lastMetricSent.factorial != metric.factorial) {
            header += FACTOR_CHANGED;
            bufferSize += 2;
        }

        // Create the byte array only with necessaries values
        ByteBuffer b = ByteBuffer.allocate(bufferSize + 4).putInt(bufferSize).put(header);

        if (lastMetricSent == null || lastMetricSent.pid != metric.pid) {
            b.putInt(metric.pid);
        }

        if (lastMetricSent == null || lastMetricSent.screenRes != metric.screenRes) {
            b.putInt(metric.screenRes);
        }

        if (lastMetricSent == null || !Arrays.equals(lastMetricSent.osName, metric.osName)) {
            b.putInt(metric.osName.length);
            b.put(metric.osName);
        }

        if (lastMetricSent == null || lastMetricSent.cpu != metric.cpu) {
            b.putDouble(metric.cpu);
        }

        if (lastMetricSent == null || lastMetricSent.id != metric.id) {
            b.putInt(metric.id);
        }

        if (lastMetricSent == null || lastMetricSent.factorial != metric.factorial) {
            b.putShort(metric.factorial);
        }

        return b.array();
    }


    /**
     * Read the inputstream and consume the first metric available.
     * Pass the last metric received to completely fill the object
     * as we don't send all field if they did'nt changed
     *
     * @param ins : the inpustream with metrics values
     * @param metricToFillWith : The metric wich allow to fill possible missing fields
     * @return the first metric
     */
    public Metric nextMetric(InputStream ins, Metric metricToFillWith) {
        try {

            // Read the first int we give us the size of the metric object
            ins.read(sIntByte);

            // Create an array to store the next metric bytes
            byte[] metric = new byte[ByteBuffer.wrap(sIntByte).getInt()];
            ins.read(metric, 0, metric.length);

            // Create the object from the array of byte
            Metric metricUnserialized = unserializedMetric(metric);

            // and fill missing field thanks to the last metric received
            fillMetricWithMissingData(metricUnserialized, metricToFillWith);

            return metricUnserialized;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * Fill missing fields of the metricToFill with the other metric object
     *
     * @param metricToFill : the metric with possible missing fields
     * @param metricToFillWith : the metric use to replace missing field
     */
    private void fillMetricWithMissingData(Metric metricToFill, Metric metricToFillWith) {
        if (metricToFillWith == null) return;
        if (metricToFill.pid < 0) metricToFill.pid = metricToFillWith.pid;
        if (metricToFill.screenRes < 0) metricToFill.screenRes = metricToFillWith.screenRes;
        if (metricToFill.osName == null) metricToFill.osName = Arrays.copyOf(metricToFillWith.osName, metricToFillWith.osName.length);
        if (metricToFill.cpu < 0) metricToFill.cpu = metricToFillWith.cpu;
        if (metricToFill.id < 0) metricToFill.id = metricToFillWith.id;
        if (metricToFill.factorial < 0) metricToFill.factorial = metricToFillWith.factorial;
    }

    /**
     * Create a new Metric object from is bytes representation
     * @param metrics: the bytes to parse
     * @return the Metric object parsed
     */
    private static Metric unserializedMetric(byte[] metrics) {
        ByteBuffer m = ByteBuffer.wrap(metrics);

        // Get the header which is the first byte
        byte header = m.get();

        Metric metric = new Metric();

        // Get the pid which is always send
        metric.pid = (header & PID_CHANGED) == PID_CHANGED ?  m.getInt() : -1;

        // Fill the metric with data if has changed else init to no value
        metric.screenRes = (header & SCREENRES_CHANGED) == SCREENRES_CHANGED ? m.getInt() : -1;

        if ((header & OSNAME_CHANGED) == OSNAME_CHANGED) {
            int stringByteLength = m.getInt();
            metric.osName = new byte[stringByteLength];
            m.get(metric.osName);
        } else {
            metric.osName = null;
        }

        metric.cpu = (header & CPU_CHANGED) == CPU_CHANGED ? m.getDouble() : -1;
        metric.id = (header & ID_CHANGED) == ID_CHANGED ? m.getInt() : -1;
        metric.factorial = (header & FACTOR_CHANGED) == FACTOR_CHANGED ? m.getShort() : -1;

        return metric;
    }

}

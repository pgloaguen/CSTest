package com.pgloaguen.csprotocolexercise;

import java.awt.*;
import java.lang.management.ManagementFactory;

/**
 * Builds Metric object efficiently by saving datas will not changed in the futur as the screen resolution, the pid or the os name.
 */
public class MetricFactory {

    private final int mScreenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
    private final int mPid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    private final byte[] mOsName = System.getProperty("os.name").getBytes();

    private int mLastMetricId = 0;
    private long mFactoriel = 0;

    /**
     *
     * @return a new Metric object
     */
    public Metric nextMetric() {
        return new Metric(getProcessId(), getMetricId(), getCpu(), getScreenResolution(), getOsName(), getFactor());
    }

    private double getCpu() {
        return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }

    private int getProcessId() {
        return mPid;
    }

    private int getScreenResolution() {
        return mScreenResolution;
    }

    private int getMetricId() {
        return mLastMetricId++;
    }

    private byte[] getOsName() { return mOsName; }

    private short getFactor() {
        short f = (short) ((mFactoriel / 1000) % 1000);
        mFactoriel += mLastMetricId;
        return f;
    }
}

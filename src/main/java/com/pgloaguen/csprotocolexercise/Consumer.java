package com.pgloaguen.csprotocolexercise;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * This class listen two Producers and write into the file each time the BUFFER_SIZE_LIMIT is reached asynchronously
 *
 */
public class Consumer{

    private boolean mIsStarted;
    private final MetricSerializer mMetricSerializer;
    private ArrayList<Thread> mThreads = new ArrayList<Thread>();

    public Consumer(int port, MetricSerializer metricSerializer) {
        mMetricSerializer = metricSerializer;
        mIsStarted = true;
        try {
            // Create the server
            final ServerSocket serverSocket = new ServerSocket(port);

            // We do it to not block the thread
            serverSocket.setSoTimeout(100);
            Thread t = new Thread(new Runnable() {
                public void run() {

                        while (mIsStarted) {
                            try {
                                final InputStream ins = serverSocket.accept().getInputStream();
                                System.out.println("New client connected");
                                startListenClient(ins);
                            } catch (InterruptedIOException e ) {
                              // Do nothing a timeout has been raised
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                }
            });
            mThreads.add(t);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListenClient(final InputStream ins) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Metric lastMetric = null;
                while (mIsStarted) {
                    Metric metric = mMetricSerializer.nextMetric(ins, lastMetric);
                    lastMetric = metric;
                    writeMetrics((metric.toString() + "\n").getBytes());
                }
            }
        });

        mThreads.add(t);
        t.start();
    }

    /**
     *
     * Write in the file cache the metrics given
     *
     * @param metrics
     */
    private void writeMetrics(byte[] metrics) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("cache", true);
            fos.write(metrics);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void destroy() {
        mIsStarted = false;
        for(Thread t : mThreads) {
            try {
                t.join(500); // Wait the thread close
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // In case 500ms was not enough -> kill the thread
            if (t.isAlive()) {
                System.out.println("Forced to kill a thread");
                t.interrupt();
            }
        }
    }
}

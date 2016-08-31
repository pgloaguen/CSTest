package com.pgloaguen.csprotocolexercise;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 *
 * The Producer will send to the server Metric objects in a random interval between 1 and 100 ms
 * To limit the number of bytes send via the network, I made a custom binary format, I also made the choice
 * to not send fields which have not changed compare to the last Metric object send
 *
 */
public class Producer {

    private final Thread mThread;
    private boolean mIsStarted;
    private Metric mLastMetricSend;
    private final MetricFactory mMetricFactory;
    private final MetricSerializer mMetricSerializer;

    public Producer(final int port, MetricFactory metricFactory, MetricSerializer metricSerializer) {
        mMetricFactory = metricFactory;
        mMetricSerializer = metricSerializer;
        mIsStarted = true;
        final Random randomExec = new Random();
        mThread = new Thread(new Runnable() {
            public void run() {
                Socket socket = null;
                try {
                    // Connect to the server
                    socket = new Socket("localhost", port);
                    while (mIsStarted) {
                        // builds the next Metric object and send it
                        if (!buildNextMetricAndSend(socket)) {
                            System.out.println("An error occurred when trying to write in the socket => destroy the producer");
                            // Here we should save the metrics in a cache and w8 the server comes back instead of just close the client
                            destroy();
                            continue;
                        }
                        try {
                            // Wait 1 to 100 ms randomly
                            Thread.sleep(randomExec.nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null) try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        mThread.start();
    }

    /**
     *
     * Generates and serializes and writes the next metric in the socket
     *
     * @param socket : the socket to write in
     * @return true if no error occurred else false if it was impossible to write in the socket
     */
    private boolean buildNextMetricAndSend(Socket socket) {
        try {

            // Create the next metric object
            Metric newMetric = mMetricFactory.nextMetric();

            // Serialized it (pass the last metric send to limit the size of data send)
            if (mMetricSerializer.serializedMetric(socket.getOutputStream(), newMetric, mLastMetricSend)) {
                mLastMetricSend = newMetric;
                return true;
            } else {
                return false;
            }


        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Call it to stop the thread
     */
    public void destroy() {
        mIsStarted = false;
        try {
            mThread.join(500); // Wait the thread close
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // In case 500ms was not enough -> kill the thread
        if (mThread.isAlive()) {
            System.out.println("Forced to kill the thread");
            mThread.interrupt();
        }
    }
}

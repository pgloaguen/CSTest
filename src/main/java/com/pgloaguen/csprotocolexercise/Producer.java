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
                        // Genererate the next Metric object and send it
                        generateMetricAndSend(socket);
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
     * Send in the socket the next Metric object
     * @param socket
     */
    private void generateMetricAndSend(Socket socket) {
        try {

            // Create the next metric object
            Metric newMetric = mMetricFactory.nextMetric();

            // Serialized it (pass the last metric send to limit the size of data send)
            byte[] serializedMetric =  mMetricSerializer.serializedMetric(newMetric, mLastMetricSend);

            // Send into the socket
            socket.getOutputStream().write(serializedMetric);

            // Just for an indication of the effectiveness of the compression
            System.out.println("Send " + serializedMetric.length + " bytes instead of " + mMetricSerializer.maxMetricSize(newMetric) +  " bytes compressed to " + (serializedMetric.length / (float)mMetricSerializer.maxMetricSize(newMetric)) * 100 + "%");

            // Save the last metric send for the next round
            mLastMetricSend = newMetric;
        } catch (IOException e) {
            e.printStackTrace();
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

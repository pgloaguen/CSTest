package com.pgloaguen.csprotocolexercise;

import java.util.Arrays;

/**
 * Is the representation of one metric
 */
public class Metric {
        public double cpu;
        public int pid;
        public int id;
        public int screenRes;
        public byte[] osName;
        public short factorial;

        public Metric() {
        }

        public Metric(int pid, int id, double cpu, int screenRes, byte[] osName, short factor) {
            this.cpu = cpu;
            this.pid = pid;
            this.id = id;
            this.screenRes = screenRes;
            this.osName = osName;
            this.factorial = factor;
        }

        @Override
        public String toString() {
            return "Metric{" +
                    "cpu=" + cpu +
                    ", PID=" + pid +
                    ", id=" + id +
                    ", SCREEN_RESOLUTION=" + screenRes +
                    ", OS_NAME=" + new String(osName) +
                    ", factorial=" + factorial +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Metric metric = (Metric) o;

            if (Double.compare(metric.cpu, cpu) != 0) return false;
            if (pid != metric.pid) return false;
            if (id != metric.id) return false;
            if (screenRes != metric.screenRes) return false;
            if (factorial != metric.factorial) return false;
            return Arrays.equals(osName, metric.osName);

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(cpu);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + pid;
            result = 31 * result + id;
            result = 31 * result + screenRes;
            result = 31 * result + Arrays.hashCode(osName);
            result = 31 * result + (int) factorial;
            return result;
        }
    }
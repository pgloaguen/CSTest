package com.pgloaguen.csprotocolexercise;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            displayHelp();
        } else if (args[0].toLowerCase().equals("-c")){
            System.out.println("Consumer started, Press enter to exit");
            Consumer c = new Consumer(Integer.parseInt(args[1]), new MetricSerializer());
            waitEnterToExit();
            System.out.println("End");
            c.destroy();
            System.exit(0);
        } else if(args[0].toLowerCase().equals("-p")) {
            System.out.println("Producer started, Press enter to exit");
            Producer p = new Producer(Integer.parseInt(args[1]), new MetricFactory(), new MetricSerializer());
            waitEnterToExit();
            System.out.println("End");
            p.destroy();
            System.exit(0);
        } else {
            displayHelp();
        }
    }

    private static void waitEnterToExit() {
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayHelp() {
        System.out.println("-c {port_number}: start a consumer \n-p {port_number}: start a producer");
    }
}
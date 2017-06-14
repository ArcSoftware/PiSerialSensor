package com.theironyard.charlotte.managers;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class ArduinoManager {
    private RaspberryPiManager piManager;

    public ArduinoManager() {
        piManager = new RaspberryPiManager();
    }

    @Autowired
    RestTemplate template;

    public void connect() throws Exception {
        String portName = "/dev/ttyUSB0";
        int portSpeed = 9600;

        System.out.println("Connecting to " + portName + " at " + portSpeed + " baud");

        CommPortIdentifier portIdentifier = null;

        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        } catch (gnu.io.NoSuchPortException e) {
            System.out.println("Port not found, listing available ports...");
            java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
            while (portEnum.hasMoreElements())
                System.out.println(portEnum.nextElement().getName());
            System.exit(1);
        }

        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(ArduinoManager.class.getName(), 2000);

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(portSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();

                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    public class SerialReader implements Runnable {
        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        public void run() {
            String weight;
            Integer readings;
            Scanner sc = new Scanner(in);

            try {
                while (sc.hasNext()) {
                    weight = sc.nextLine();
                    readings = Integer.valueOf(weight);
                    if (readings >= 10 && readings <= 100) {
                        ArduinoManager.this.template.postForLocation("https://sharedspace.herokuapp.com/addCoffee",
                                "post");
                        String text = ":coffee: The coffee is low! Creating a new task to refill it!";
                        System.out.println("Coffee is low! \n Creating task, bossing around Slack bot..." +
                                " \n Current Reading: " + readings);
                        Map<String, String> map = new HashMap<>();
                        map.put("text", text);

                        ArduinoManager.this.template.postForObject(
                                "https://hooks.slack.com/services/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",
                                map, String.class);
                        piManager.alert(10, "red");
                        piManager.lowLED();
                    } else if (readings >= 201) {
                        piManager.allOff();
                        String text = "Coffee has been refilled! :parrot:";
                        System.out.println("Coffee is now Full. \n Creating task, bossing around Slack bot..." +
                                "\n Current Reading: " + readings);
                        Map<String, String> map = new HashMap<>();
                        map.put("text", text);

                        ArduinoManager.this.template.postForObject(
                                "https://hooks.slack.com/services/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",
                                map, String.class);
                        piManager.alert(10, "green");
                        piManager.allOff();
                    } else {
                        System.out.println(readings);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SerialWriter implements Runnable {
        OutputStream out;

        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        public void run() {
            try {
                int c = 0;
                while ((c = System.in.read()) > -1) {
                    this.out.write(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
//    public static String stateCheck(byte[] buffer) {
//        while (true) {
//            if (buffer. >= 1 && buffer <= 100)
//        }
//
//    }
}

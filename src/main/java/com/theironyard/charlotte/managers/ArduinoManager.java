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
                        postRequest(text, "low", readings);
                    } else if (readings >= 201) {
                        piManager.allOff();
                        String text = "Coffee has been refilled! :parrot:";
                        postRequest(text, "full", readings);
                    } else if (readings == 0) {
                        System.err.println("No weight on the sensor!");

                    } else {
                        System.out.println(readings);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void postRequest(String text, String status, Integer readings) throws InterruptedException {
            String taskCreation = status.equalsIgnoreCase("low") ? "Creating Task..." : "";
            System.out.format("Coffee is now %s. \n Bossing around Slack bot... \n %s" +
                    "\n Current Reading: " + readings + ".\n", status, taskCreation);
            Map<String, String> map = new HashMap<>();
            map.put("text", text);

            ArduinoManager.this.template.postForObject(
                    System.getenv("SLACK_API_URL"),
                    map, String.class);

            if (status.equalsIgnoreCase("low")) {
                piManager.alert(10, "red");
                piManager.lowLED();
            } else if (status.equalsIgnoreCase("full")){
                piManager.alert(10, "green");
                piManager.allOff();
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
}

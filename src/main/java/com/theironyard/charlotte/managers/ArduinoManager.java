package com.theironyard.charlotte.managers;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ArduinoManager {

    public static void connect() throws Exception {
        String portName = "/dev/ttyUSB0";
        int portSpeed = 9600;

        System.out.println("Connecting to " + portName + " at " + portSpeed + " baud");

        CommPortIdentifier portIdentifier = null;

//        try {
//            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
//        } catch (gnu.io.NoSuchPortException e) {
//            System.out.println("Port not found, listing available ports...");
//            java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
//            while (portEnum.hasMoreElements())
//                System.out.println(portEnum.nextElement().getName());
//            System.exit(1);
//        }

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

    public static class SerialReader implements Runnable {
        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while ((len = this.in.read(buffer)) > -1) {
                    // issue post request after you get the string value
                    // of the currently depressed thing.
                    System.out.println(len + "This is the Len");
                    System.out.print(new String(buffer, 0, len));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SerialWriter implements Runnable {
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

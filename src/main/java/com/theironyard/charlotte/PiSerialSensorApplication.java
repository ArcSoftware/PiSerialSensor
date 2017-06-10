package com.theironyard.charlotte;

import com.theironyard.charlotte.managers.ArduinoManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PiSerialSensorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiSerialSensorApplication.class, args);
		if (args.length != 2) {
			System.out.println("Usage: ArduRasPi portName portSpeed");
			System.out.println("Example: ArduRasPi /dev/ttyUSB0 9600");
			System.exit(1);
		}

		try {
			(new ArduinoManager()).connect(args[0], Integer.parseInt(args[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

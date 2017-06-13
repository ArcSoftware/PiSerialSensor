package com.theironyard.charlotte;

import com.theironyard.charlotte.managers.ArduinoManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PiSerialSensorApplication {

	public PiSerialSensorApplication(ArduinoManager manager) {
		try {
			manager.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(PiSerialSensorApplication.class, args);
	}

	//to run: sudo java -jar -Djava.library.path=/usr/lib/jni PiSerialSensor-0.0.1-SNAPSHOT.jar
}

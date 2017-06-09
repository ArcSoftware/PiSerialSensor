package com.theironyard.charlotte.managers;


import com.pi4j.io.gpio.*;

public class RaspberryPiManager {
    private final GpioController gpio;
    private GpioPinDigitalOutput testLED;

    public RaspberryPiManager() {
        gpio = GpioFactory.getInstance();
        testLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "testLED", PinState.LOW);

    }
}

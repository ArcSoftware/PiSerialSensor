package com.theironyard.charlotte.managers;


import com.pi4j.io.gpio.*;

public class RaspberryPiManager {
    private final GpioController gpio;
    private GpioPinDigitalOutput redLED;
    private GpioPinDigitalOutput greenLED;
    private GpioPinDigitalOutput blueLED;
    private GpioPinDigitalOutput whiteLED;

    public RaspberryPiManager() {
        gpio = GpioFactory.getInstance();
        redLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Red LED", PinState.LOW);
        greenLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "Green LED", PinState.LOW);
        blueLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Blue LED", PinState.LOW);
        whiteLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "White LED", PinState.LOW);
    }

    public void alert(int duration, String color) throws InterruptedException {
        if (duration > 0) {
            alert(-- duration, color);
        }
        if (color.equalsIgnoreCase("red")) {
            if (duration%2 == 0) {
                redLED.blink(100, 400);
                Thread.sleep(400);
            } else {
                whiteLED.blink(100, 400);
                Thread.sleep(400);
            }
        }
        else {
            if (duration%2 == 0) {
                greenLED.blink(100, 400);
                Thread.sleep(400);
            } else {
                blueLED.blink(100, 400);
                Thread.sleep(400);
            }
        }

    }
    public void allOff() {
        if (whiteLED.isHigh()) {whiteLED.toggle();}
        if (redLED.isHigh()) {redLED.toggle();}
        if (greenLED.isHigh()) {greenLED.toggle();}
        if (blueLED.isHigh()) {blueLED.toggle();}
    }


}

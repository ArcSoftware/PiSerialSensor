package com.theironyard.charlotte;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Jake on 6/9/17.
 */
public class PiSerialSensorController {
    private RaspberryPiManager piManager;

    public PiSerialSensorController() {
        piManager = new RaspberryPiManager();
    }
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(String led){
        return "index";
    }

}

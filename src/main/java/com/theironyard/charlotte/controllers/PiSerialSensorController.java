package com.theironyard.charlotte.controllers;

import com.theironyard.charlotte.managers.RaspberryPiManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
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

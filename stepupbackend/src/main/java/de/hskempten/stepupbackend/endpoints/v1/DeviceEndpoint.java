package de.hskempten.stepupbackend.endpoints;

import de.hskempten.stepupbackend.controllers.DeviceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceEndpoint {

    @Autowired
    DeviceController deviceController;

    @PostMapping
    public 
}

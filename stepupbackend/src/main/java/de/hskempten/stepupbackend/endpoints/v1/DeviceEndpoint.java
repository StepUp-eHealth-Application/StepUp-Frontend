package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.DeviceController;
import de.hskempten.stepupbackend.dto.DeviceDTO;
import org.hl7.fhir.r4.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceEndpoint {

    @Autowired
    DeviceController deviceController;

    @GetMapping("/{id}")
    public HttpEntity<DeviceDTO> getDeviceById(@PathVariable("id") String id) {
        System.out.println("ID: " + id);
        DeviceDTO device = deviceController.getDeviceById(id);

        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping
    public HttpEntity<DeviceDTO> createDevice(@RequestBody DeviceDTO device) {
        DeviceDTO fhirDevice = deviceController.createDevice(device);

        return new ResponseEntity<>(fhirDevice, HttpStatus.OK);
    }
}

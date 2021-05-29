package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.PatientController;
import de.hskempten.stepupbackend.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientEndpoint {

    @Autowired
    PatientController patientController;

    @PostMapping
    public HttpEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        patientDTO = patientController.createPatient(patientDTO);
        return new ResponseEntity<>(patientDTO, HttpStatus.CREATED);
    }
}

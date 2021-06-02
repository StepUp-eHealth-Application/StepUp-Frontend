package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.PatientController;
import de.hskempten.stepupbackend.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientEndpoint {

    @Autowired
    PatientController patientController;

    @GetMapping
    public HttpEntity<List<PatientDTO>> getPatients() {
        var patients = patientController.getPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<PatientDTO> getPatientById(@PathVariable String id) {
        PatientDTO patientDTO = patientController.getPatientById(id);
        if (patientDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(patientDTO, HttpStatus.OK);
    }

    @PostMapping
    public HttpEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        patientDTO = patientController.createPatient(patientDTO);
        return new ResponseEntity<>(patientDTO, HttpStatus.CREATED);
    }

}

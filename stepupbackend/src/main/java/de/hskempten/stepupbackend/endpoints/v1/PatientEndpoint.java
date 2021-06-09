package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.PatientController;
import de.hskempten.stepupbackend.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("name/{name}")
    public HttpEntity<List<PatientDTO>> getPatientByName(@PathVariable("name") String name) {
        List<PatientDTO> patientDTO = patientController.getPatientByName(name);
        return new ResponseEntity<>(patientDTO, HttpStatus.OK);
    }

    @GetMapping("address/{address}")
    public HttpEntity<List<PatientDTO>> getPatientByStreet(@PathVariable("address") String address) {
        List<PatientDTO> patientDTOS = patientController.getPatientsByAddress(address);
        return new ResponseEntity<>(patientDTOS, HttpStatus.OK);
    }

    @GetMapping("gender/{gender}")
    public HttpEntity<List<PatientDTO>> getPatientByGender(@PathVariable String gender) {
        List<PatientDTO> patientDTOS = patientController.getPatientsByGender(gender);
        return new ResponseEntity<>(patientDTOS, HttpStatus.OK);
    }

    @PostMapping
    public HttpEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        patientDTO = patientController.createPatient(patientDTO);
        return new ResponseEntity<>(patientDTO, HttpStatus.CREATED);
    }

}

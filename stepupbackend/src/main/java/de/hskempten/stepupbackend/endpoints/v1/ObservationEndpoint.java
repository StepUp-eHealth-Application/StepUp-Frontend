package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.ObservationController;
import de.hskempten.stepupbackend.dto.StepsObservationDTO;
import de.hskempten.stepupbackend.dto.WeightObservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/observation/")
public class ObservationEndpoint {

    @Autowired
    private ObservationController observationController;

    @PostMapping("weight")
    public HttpEntity<WeightObservationDTO> createWeightObservation(@RequestBody WeightObservationDTO weightObservationDTO) {
        weightObservationDTO = observationController.addWeightObservation(weightObservationDTO);
        return new ResponseEntity<>(weightObservationDTO, HttpStatus.CREATED);
    }

    @GetMapping("weight/patient/{id}")
    public HttpEntity<List<WeightObservationDTO>> getWeightsByPatientId(@PathVariable String id) {
        List<WeightObservationDTO> observations = observationController.getWeightsByPatientId(id);

        return new ResponseEntity<>(observations, HttpStatus.OK);
    }

    @PostMapping("steps")
    public HttpEntity<StepsObservationDTO> addStepsObservation(@RequestBody StepsObservationDTO stepsObservationDTO) {
        stepsObservationDTO = observationController.addStepsObservation(stepsObservationDTO);
        return new ResponseEntity<>(stepsObservationDTO, HttpStatus.CREATED);
    }

    @GetMapping("setps/patient/{id}")
    public HttpEntity<List<StepsObservationDTO>> getStepsByPatientId(@PathVariable String id) {
        List<StepsObservationDTO> observations = observationController.getStepsByPatientId(id);

        return new ResponseEntity<>(observations, HttpStatus.OK);
    }
}

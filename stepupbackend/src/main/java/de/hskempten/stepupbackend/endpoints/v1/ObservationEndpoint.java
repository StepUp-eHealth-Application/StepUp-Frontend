package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.ObservationController;
import de.hskempten.stepupbackend.dto.*;
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

    @GetMapping("weight/{id}")
    public HttpEntity<WeightObservationDTO> getWeightById(@PathVariable String id) {
        WeightObservationDTO weightObservationDTO = observationController.getWeightObservationById(id);
        return new ResponseEntity<>(weightObservationDTO, HttpStatus.OK);
    }

    @PostMapping("weight")
    public HttpEntity<WeightObservationDTO> createWeightObservation(@RequestBody WeightObservationDTO weightObservationDTO) {
        weightObservationDTO = observationController.addWeightObservation(weightObservationDTO);
        return new ResponseEntity<>(weightObservationDTO, HttpStatus.CREATED);
    }

    @GetMapping("weight/patient/{patientId}")
    public HttpEntity<List<WeightObservationDTO>> getWeightsByPatientId(@PathVariable String patientId) {
        List<WeightObservationDTO> observations = observationController.getWeightsByPatientId(patientId);

        return new ResponseEntity<>(observations, HttpStatus.OK);
    }

    @GetMapping("weight/goal/{goalId}/patient/{patientId}")
    public HttpEntity<List<WeightObservationDTO>> getWeightsByGoalId(@PathVariable String goalId, @PathVariable String patientId) {
        List<WeightObservationDTO> observations = observationController.getWeightsByGoalId(goalId, patientId);

        return new ResponseEntity<>(observations, HttpStatus.OK);
    }

    @PostMapping("weight/date")
    public HttpEntity<List<WeightObservationDTO>> getWeightsByDate(@RequestBody DateDTO dateDTO) {
        List<WeightObservationDTO> observations = observationController.getWeightsByDate(dateDTO);

        return new ResponseEntity<>(observations, HttpStatus.OK);
    }

    @GetMapping("steps/{id}")
    public HttpEntity<StepsObservationDTO> getStepsById(@PathVariable String id) {
        StepsObservationDTO stepsObservationDTO = observationController.getStepsObservationById(id);
        return new ResponseEntity<>(stepsObservationDTO, HttpStatus.OK);
    }

    @GetMapping("steps/goal/{goalId}/patient/{patientId}")
    public HttpEntity<List<StepsObservationDTO>> getStepsByGoalId(@PathVariable String goalId, @PathVariable String patientId) {
        List<StepsObservationDTO> stepsObservationDTO = observationController.getStepsByGoalId(goalId, patientId);
        return new ResponseEntity<>(stepsObservationDTO, HttpStatus.OK);
    }

    @PostMapping("steps")
    public HttpEntity<StepsObservationDTO> addStepsObservation(@RequestBody StepsObservationDTO stepsObservationDTO) {
        stepsObservationDTO = observationController.addStepsObservation(stepsObservationDTO);
        return new ResponseEntity<>(stepsObservationDTO, HttpStatus.CREATED);
    }

    @GetMapping("steps/patient/{patientId}")
    public HttpEntity<List<StepsObservationDTO>> getStepsByPatientId(@PathVariable String patientId) {
        List<StepsObservationDTO> observations = observationController.getStepsByPatientId(patientId);

        return new ResponseEntity<>(observations, HttpStatus.OK);
    }

    @PostMapping("steps/date")
    public HttpEntity<List<StepsObservationDTO>> getStepsByDate(@RequestBody DateDTO dateDTO) {
        List<StepsObservationDTO> observations = observationController.getStepsByDate(dateDTO);

        return new ResponseEntity<>(observations, HttpStatus.OK);
    }
}

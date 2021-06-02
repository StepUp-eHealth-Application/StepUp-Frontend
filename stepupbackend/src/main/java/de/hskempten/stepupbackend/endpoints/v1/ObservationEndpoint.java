package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.ObservationController;
import de.hskempten.stepupbackend.dto.WeightObservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}

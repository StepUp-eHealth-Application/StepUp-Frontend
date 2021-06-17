package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.CompositionController;
import de.hskempten.stepupbackend.dto.CompositionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/composition")
public class CompositionEndpoint {

    @Autowired
    private CompositionController compositionController;

    @PostMapping
    public HttpEntity<CompositionDTO> createComposition(@RequestBody CompositionDTO compositionDTO) {
        compositionDTO = compositionController.createComposition(compositionDTO);
        return new ResponseEntity<>(compositionDTO, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public HttpEntity<CompositionDTO> getCompositionById(@PathVariable String id) {
        CompositionDTO compositionDTO = compositionController.getCompositionById(id);
        return new ResponseEntity<>(compositionDTO, HttpStatus.OK);
    }
}

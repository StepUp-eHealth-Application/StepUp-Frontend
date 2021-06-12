package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.CompositionController;
import de.hskempten.stepupbackend.dto.CompositionDTO;
import de.hskempten.stepupbackend.dto.DeviceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/composition")
public class CompositionEndpoint {

    @Autowired
    private CompositionController compositionController;

    @PostMapping
    public HttpEntity<CompositionDTO> createComposition(CompositionDTO compositionDTO) {
        compositionDTO = compositionController.createComposition(compositionDTO);
    }
}

package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.PractitionerController;
import de.hskempten.stepupbackend.dto.PractitionerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/practitioner")
public class PractitionerEndpoint {

    @Autowired
    PractitionerController practitionerController;

    @GetMapping
    public HttpEntity<List<PractitionerDTO>> getAllPractitioners() {
        List<PractitionerDTO> practitioners = practitionerController.getAllPractitioners();
        return new ResponseEntity<>(practitioners, HttpStatus.OK);
    }
}

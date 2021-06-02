package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WeightObservationDTO {

    private String id;
    private float weight; // in kg
    private LocalDate date;
    private String patientID;
    private String fhirServer;

    public WeightObservationDTO(@JsonProperty("weight") float weight,
                                @JsonProperty("date") LocalDate date,
                                @JsonProperty("patientID") String patientID,
                                @JsonProperty("fhirServer") String fhirServer) {
        this.weight = weight;
        this.date = date;
        this.patientID = patientID;
        this.fhirServer = fhirServer;
    }
}

package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WeightObservationDTO {

    private String id;
    private float weight; // in kg
    private Date date;
    private String patientID;
    private String fhirServer;

    public WeightObservationDTO(@JsonProperty("weight") float weight,
                                @JsonProperty("date") Date date,
                                @JsonProperty("patientID") String patientID,
                                @JsonProperty("fhirServer") String fhirServer) {
        this.weight = weight;
        this.date = date;
        this.patientID = patientID;
        this.fhirServer = fhirServer;
    }
}

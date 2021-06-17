package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StepsObservationDTO {

    private String id;
    private int steps; // per day
    private LocalDate date;
    private String patientID;
    private String fhirServer;
    private String deviceID;

    public StepsObservationDTO(@JsonProperty("steps") int steps,
                                @JsonProperty("date") LocalDate date,
                                @JsonProperty("patientID") String patientID,
                                @JsonProperty("fhirServer") String fhirServer,
                               @JsonProperty("deviceID") String deviceID) {
        this.steps = steps;
        this.date = date;
        this.patientID = patientID;
        this.fhirServer = fhirServer;
        this.deviceID = deviceID;
    }

}

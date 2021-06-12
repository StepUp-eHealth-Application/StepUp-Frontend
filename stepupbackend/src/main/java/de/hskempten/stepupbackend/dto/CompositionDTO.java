package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CompositionDTO {

    String id;
    String patientId;
    LocalDate date;

    @JsonCreator
    public CompositionDTO(@JsonProperty("patientId") String patientId,
                          @JsonProperty("date") LocalDate date) {
        this.patientId = patientId;
        this.date = date;
    }
}

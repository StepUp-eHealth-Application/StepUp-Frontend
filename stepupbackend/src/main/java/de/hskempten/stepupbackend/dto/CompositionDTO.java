package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CompositionDTO {

    String id;
    String patientId;
    Date date;

    @JsonCreator
    public CompositionDTO(@JsonProperty("patientId") String patientId,
                          @JsonProperty("date") Date date,
                          @JsonProperty("id") String id) {
        this.patientId = patientId;
        this.date = date;
        this.id = id;
    }
}

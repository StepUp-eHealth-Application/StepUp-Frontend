package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class DateDTO {

    private String patientId;
    private LocalDate date;

    @JsonCreator
    public DateDTO(@JsonProperty("date") LocalDate date,
                   @JsonProperty("patientId") String patientId) {
        this.date = date;
        this.patientId = patientId;
    }
}

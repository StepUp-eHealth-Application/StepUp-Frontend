package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class StepsGoalDTO {
    private String id;
    private String description;
    private String patientId;
    private Date dueDate;
    private int stepsGoal;

    public StepsGoalDTO(@JsonProperty("id") String id,
                         @JsonProperty("description") String description,
                         @JsonProperty("patientId") String patientId,
                         @JsonProperty("dueDate") Date dueDate,
                         @JsonProperty("stepsGoal") int stepsGoal) {
        this.id = id;
        this.description = description;
        this.patientId = patientId;
        this.dueDate = dueDate;
        this.stepsGoal = stepsGoal;
    }
}

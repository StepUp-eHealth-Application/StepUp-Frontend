package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PractitionerDTO {

    private String id;
    private String firstName;
    private String lastName;

    @JsonCreator
    public PractitionerDTO(@JsonProperty String id,
                           @JsonProperty String firstName,
                           @JsonProperty String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

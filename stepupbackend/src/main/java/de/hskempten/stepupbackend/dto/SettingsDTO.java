package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class SettingsDTO {

    private String fhirUrl;

    public SettingsDTO(@JsonProperty("fhirUrl") String fhirUrl) {
        this.fhirUrl = fhirUrl;
    }
}

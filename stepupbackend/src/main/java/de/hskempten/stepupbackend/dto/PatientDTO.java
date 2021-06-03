package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatientDTO {

    private String id;

    private String firstName;
    private String lastName;

    private String gender;

    private String street;
    private String postalCode;
    private String city;
    private String country;

    private String fhirServer;

    public PatientDTO(@JsonProperty("firstName") String firstName,
                      @JsonProperty("lastName") String lastName,
                      @JsonProperty("gender") String gender,
                      @JsonProperty("street") String street,
                      @JsonProperty("postalCode") String postalCode,
                      @JsonProperty("city") String city,
                      @JsonProperty("country") String country,
                      @JsonProperty("fhirServer") String fhirServer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.fhirServer = fhirServer;
    }

    public String getDisplayAddress() {
        return street + ", " + postalCode + " " + city + ", " + country;
    }
}

package de.hskempten.stepupbackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Device;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@NoArgsConstructor
public class DeviceDTO {

    public String id;
    public String name;
    public String manufacturer;
    public String status;
    public String type;
    public String note;

    public String patientID;
    public String fhirServer;

    @JsonCreator
    public DeviceDTO(@JsonProperty("id") String id,
                     @JsonProperty("name") String name,
                     @JsonProperty("manufacturer") String manufacturer,
                     @JsonProperty("status") String status,
                     @JsonProperty("type") String type,
                     @JsonProperty("note") String note,
                     @JsonProperty("patientID") String patientID,
                     @JsonProperty("fhirServer") String fhirServer) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.status = status;
        this.type = type;
        this.note = note;
        this.patientID = patientID;
        this.fhirServer = fhirServer;
    }
}

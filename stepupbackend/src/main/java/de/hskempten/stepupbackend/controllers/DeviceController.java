package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.DeviceDTO;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
public class DeviceController {

    @Autowired
    private SettingsController settingsController;

    public DeviceDTO createDevice(DeviceDTO device) {
        Device fhirDevice = new Device();

        // Setting status
        HashMap<String, Device.FHIRDeviceStatus> status = new HashMap<>();
        status.put("Aktiv", Device.FHIRDeviceStatus.ACTIVE);
        status.put("Inaktiv", Device.FHIRDeviceStatus.INACTIVE);
        status.put("Fehlerhaft", Device.FHIRDeviceStatus.ENTEREDINERROR);
        status.put("Unbekannt", Device.FHIRDeviceStatus.UNKNOWN);

        fhirDevice.setStatus(status.get(device.status));

        // Setting the manufacturer
        fhirDevice.setManufacturer(device.manufacturer);

        // Setting the device name
        fhirDevice.addDeviceName()
            .setName(device.name)
            .setType(Device.DeviceNameType.PATIENTREPORTEDNAME);

        // Setting device type
        CodeableConcept deviceTypeConcept = new CodeableConcept();

        HashMap<String, String> deviceTypeCodingValues = new HashMap<>();
        deviceTypeCodingValues.put("Waage", "5042005");
        deviceTypeCodingValues.put("Schrittzähler", "XXXXXX"); // TODO: find code

        Coding deviceTypeCoding = new Coding();
        deviceTypeCoding.setCode(deviceTypeCodingValues.get(device.type));
        deviceTypeCoding.setDisplay(device.type);
        deviceTypeCoding.setSystem("http://snomed.info/sct");

        deviceTypeConcept.addCoding(deviceTypeCoding);
        deviceTypeConcept.setText("Art des Gesundheitgeräts");

        fhirDevice.setType(deviceTypeConcept);

        // Creating note for device
        Annotation deviceNote = new Annotation();
        deviceNote.setText(device.note);

        // Creating reference to patient
        Reference patientReference;
        patientReference = new Reference(device.patientID);
        patientReference.setType("Patient");

        // TODO: set reference to patient
        //deviceNote.setAuthor(patientReference);
        //fhirDevice.setPatient(patientReference);

        // Setting note for device
        fhirDevice.addNote(deviceNote);

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(device.fhirServer);

        Device createDevice = (Device) client.create().resource(fhirDevice).execute().getResource();

        device.id = createDevice.getIdElement().getIdPart();
        return device;
    }

    public DeviceDTO getDeviceById(String id) {
        String fhirServer = settingsController.getFhirServerUrl();

        Device device = new Device();
        device.setId(id);

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Device fhirDevice = (Device) client.create().resource(device).execute().getResource();

        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setId(fhirDevice.getId());

        if (!fhirDevice.getDeviceName().isEmpty()) {
            deviceDTO.setName(fhirDevice.getDeviceName().get(0).getName());
        } else {
            deviceDTO.setName("No Device Name");
        }

        deviceDTO.setManufacturer(fhirDevice.getManufacturer());

        if (fhirDevice.getStatus() != null) {
            deviceDTO.setStatus(fhirDevice.getStatus().getDisplay());
        }

        if (fhirDevice.getType() != null) {
            deviceDTO.setType(fhirDevice.getType().getText());
        }

        if (!fhirDevice.getNote().isEmpty()) {
            deviceDTO.setNote(fhirDevice.getNote().get(0).getText());
        }

        deviceDTO.setFhirServer(fhirServer);

        return deviceDTO;
    }
}

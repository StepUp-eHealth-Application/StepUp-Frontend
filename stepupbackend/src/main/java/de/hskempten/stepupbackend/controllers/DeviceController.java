package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.DeviceDTO;
import de.hskempten.stepupbackend.dto.PatientDTO;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class DeviceController {

    @Autowired
    private SettingsController settingsController;

    @Autowired
    private PatientController patientController;

    public DeviceDTO createDevice(DeviceDTO device) {
        Device fhirDevice = new Device();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(device.fhirServer);

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

        Patient patient = (Patient) patientController.searchPatientById(device.patientID, device.fhirServer, client).getEntryFirstRep().getResource();

        // Creating reference to patient
        Reference patientReference;
        patientReference = new Reference(patient.getId());
        patientReference.setType("Patient");

        deviceNote.setAuthor(patientReference);
        fhirDevice.setPatient(patientReference);

        // Setting note for device
        fhirDevice.addNote(deviceNote);



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

        Bundle bundle = searchDeviceById(id, fhirServer, client);
        if (bundle == null) {
            return null;
        }

        var entry = bundle.getEntryFirstRep();
        if (entry == null) {
            return null;
        }
        Device fhirDevice = (Device) entry.getResource();

        DeviceDTO deviceDTO = convertToDeviceDTO(fhirServer, fhirDevice);

        return deviceDTO;
    }

    public List<DeviceDTO> getAllDevices() {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle bundle = client
            .search()
            .forResource(Device.class)
            .returnBundle(Bundle.class)
            .execute();

        List<DeviceDTO> retDevices = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Device device = (Device) entry.getResource();

            DeviceDTO deviceDto = convertToDeviceDTO(fhirServer, device);
            retDevices.add(deviceDto);
        }

        return retDevices;
    }

    private DeviceDTO convertToDeviceDTO(String fhirServer, Device device) {
        DeviceDTO deviceDTO = new DeviceDTO();

        deviceDTO.setId(device.getIdElement().getIdPart());

        if (!device.getDeviceName().isEmpty()) {
            deviceDTO.setName(device.getDeviceName().get(0).getName());
        }
        deviceDTO.setManufacturer(device.getManufacturer());

        HashMap<Device.FHIRDeviceStatus, String> status = new HashMap<>();
        status.put(Device.FHIRDeviceStatus.ACTIVE, "Aktiv");
        status.put(Device.FHIRDeviceStatus.INACTIVE, "Inaktiv");
        status.put(Device.FHIRDeviceStatus.ENTEREDINERROR, "Fehlerhaft");
        status.put(Device.FHIRDeviceStatus.UNKNOWN, "Unbekannt");
        deviceDTO.setStatus(status.get(device.getStatus()));

        if (!device.getType().getCoding().isEmpty()) {
            deviceDTO.setType(device.getType().getCoding().get(0).getDisplay());
        }

        if (!device.getNote().isEmpty()) {
            deviceDTO.setNote(device.getNote().get(0).getText());
        }

        String ref = device.getPatient().getReference();
        if (ref != null) {
            ref = ref.replace("Patient/", "");
        }
        deviceDTO.setPatientID(ref);

        deviceDTO.setFhirServer(fhirServer);

        return deviceDTO;
    }

    public Bundle searchDeviceById(String id, String fhirServer, IGenericClient client) {
        String searchUrl = fhirServer + "/Device?_id=" + id;
        Bundle response = client.search()
            .byUrl(searchUrl)
            .returnBundle(Bundle.class)
            .execute();
        if (response.getTotal() <= 0) {
            return null;
        }
        return response;
    }
}

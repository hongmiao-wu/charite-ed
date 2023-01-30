package com.wise23.chariteed.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import com.wise23.chariteed.constant.GetPropertiesBean;
import com.wise23.chariteed.config.FhirConfig;
import com.wise23.chariteed.model.Instruction;
import com.wise23.chariteed.repository.InstructionRepository;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class InstructionService {

    @Autowired
    InstructionRepository instructionRepository;


    public FhirContext fhirContext;

    public IGenericClient client;

    public InstructionService() {
        this.fhirContext = FhirConfig.getFhirContext();
        this.client = fhirContext.newRestfulGenericClient(GetPropertiesBean.getTestserverURL());
    }

    public Instruction saveInstruction(String username, Instruction instruction) {
        instruction.setCreatedAt(LocalDateTime.now());
        instruction.setCreatedBy(username);
        return instructionRepository.save(instruction);
    }

    public Instruction getInstruction(Long id) {
        return instructionRepository.findById(id).orElse(null);
    }

    public List<Instruction> getAllInstructions() {
        return instructionRepository.findAll();
    }

    public void deleteInstruction(Long id) {
        instructionRepository.deleteById(id);
    }

    public List<Instruction> fetchHapiFhirInstructionsBundle() {
        List<IBaseResource> medicalStatements = new ArrayList<>();
        List<Instruction> instructions = new ArrayList<>();

        Bundle bundle = client.search().forResource(MedicationStatement.class).returnBundle(Bundle.class).execute();
        medicalStatements.addAll(BundleUtil.toListOfResources(fhirContext, bundle));

        for (IBaseResource resource : medicalStatements) {
            MedicationStatement ms = (MedicationStatement) resource;

            Instruction instruction = convertMedicationStatementToInstruction(ms);
            instructions.add(instruction);
        }
        return instructions;

    }

    public Instruction fetchHapiFhirInstructionByFhirId(Long medStatfhirID) {
        MedicationStatement ms = client.read().resource(MedicationStatement.class).withId(medStatfhirID.toString()).prettyPrint().execute();
        return convertMedicationStatementToInstruction(ms);
    }

    public Instruction convertMedicationStatementToInstruction(MedicationStatement ms) {
        Instruction instruction = new Instruction();
        instruction.setTitle(ms.getIdElement().getIdPart());
        String content;

        //hapi fhir server data is inconsistent:
        //sometimes MedicationCodeableConcept is present,
        //and sometimes MedicationReference is present instead...
        try {
            content = ms.getMedicationCodeableConcept().getText();
        }
        catch (Exception e) {
            log.debug(e.getMessage());
            content = ms.getMedicationReference().getDisplay();

        }
        if (ms.getDosage().size() > 0) {
            content += " " + ms.getDosage().get(0).getText();
        }
        instruction.setContent(content);

        return instruction;
    }
}

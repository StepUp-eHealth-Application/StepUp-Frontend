package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.GoalController;
import de.hskempten.stepupbackend.dto.StepsGoalDTO;
import de.hskempten.stepupbackend.dto.WeightGoalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goal/")
public class GoalEndpoint {

    @Autowired
    private GoalController goalController;

    @GetMapping("weight/{id}")
    public HttpEntity<WeightGoalDTO> getWeightGoalById(@PathVariable String id) {
        WeightGoalDTO weightGoalDTO = goalController.getWeightGoalById(id);
        return new ResponseEntity<>(weightGoalDTO, HttpStatus.OK);
    }

    @GetMapping("weight/patient/{patientId}")
    public HttpEntity<List<WeightGoalDTO>> getAllWeightGoalsByPatientId(@PathVariable String patientId) {
        List<WeightGoalDTO> weightGoals = goalController.getAllWeightGoalsByPatientId(patientId);

        return new ResponseEntity<>(weightGoals, HttpStatus.OK);
    }

    @PostMapping("weight")
    public HttpEntity<WeightGoalDTO> addWeightGoal(@RequestBody WeightGoalDTO weightGoalDTO) {
        weightGoalDTO = goalController.addWeightGoal(weightGoalDTO);
        return new ResponseEntity<>(weightGoalDTO, HttpStatus.CREATED);
    }

    @PutMapping("weight/{id}/")
    public HttpEntity<WeightGoalDTO> updateWeightGoal(@PathVariable String id, @RequestBody WeightGoalDTO weightGoalDTO) {
        weightGoalDTO = goalController.updateWeightGoal(id, weightGoalDTO);
        return new ResponseEntity<>(weightGoalDTO, HttpStatus.OK);
    }

    @GetMapping("steps/patient/{patientId}")
    public HttpEntity<List<StepsGoalDTO>> getAllStepsGoalsByPatientId(@PathVariable String patientId) {
        List<StepsGoalDTO> weightGoals = goalController.getAllStepsGoalsByPatientId(patientId);

        return new ResponseEntity<>(weightGoals, HttpStatus.OK);
    }

    @PostMapping("steps")
    public HttpEntity<StepsGoalDTO> addStepsGoal(@RequestBody StepsGoalDTO stepsGoalDTO) {
        stepsGoalDTO = goalController.addStepsGoal(stepsGoalDTO);
        return new ResponseEntity<>(stepsGoalDTO, HttpStatus.CREATED);
    }

    @PutMapping("steps/{id}/")
    public HttpEntity<StepsGoalDTO> updateStepsGoal(@PathVariable String id, @RequestBody StepsGoalDTO stepsGoalDTO) {
        stepsGoalDTO = goalController.updateStepsGoal(id, stepsGoalDTO);
        return new ResponseEntity<>(stepsGoalDTO, HttpStatus.OK);
    }
}

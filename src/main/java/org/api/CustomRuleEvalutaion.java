package org.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class CustomRuleEvalutaion {
    //explanation of @JsonIgnore
    //source: https://www.baeldung.com/jackson-annotations
    @JsonIgnore
    private ArrayList<String> ruleViolationExceptions;
    @JsonIgnore
    private ArrayList<String> ruleViolation;
    private String compliant;

    public CustomRuleEvalutaion(ArrayList<String> ruleViolationExceptions, ArrayList<String> ruleViolation, String compliant) {
        this.ruleViolationExceptions = ruleViolationExceptions;
        this.ruleViolation = ruleViolation;
        this.compliant = compliant;
    }

    public void performDifferenceRuleViolation() {
        if (compliant.equalsIgnoreCase("true")) {
            //let "compliant" attribute unchanged
        } else {
            //remove direct rules
            ruleViolation.removeAll(ruleViolationExceptions);
            //remove all subset rules
            for (String rule : ruleViolationExceptions) {
                ruleViolation.removeIf(n -> n.contains(rule));
            }
            if (ruleViolation.isEmpty()) {
                compliant = "true";
            } else {
                compliant = "false";
            }
        }
    }

}

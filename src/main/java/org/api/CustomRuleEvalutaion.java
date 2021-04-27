package org.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CustomRuleEvalutaion {
    //explanation of @JsonIgnore
    //https://www.baeldung.com/jackson-annotations
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
        //decision logic agreed on google docs
        if (compliant.equalsIgnoreCase("true")) {
            //do nothing special
        } else {
            ruleViolation.removeAll(ruleViolationExceptions);
            if (ruleViolation.isEmpty()) {
                compliant = "true";
            } else {
                compliant = "false";
            }
        }
    }

}

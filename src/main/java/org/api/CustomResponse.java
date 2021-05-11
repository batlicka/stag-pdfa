package org.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//using of Lombok reduce number of rows, It generate getters and setters for all the attributes of this class
@Getter
@Setter
public class CustomResponse implements Serializable {
    private String compliant;
    //source: https://www.baeldung.com/jackson-annotations
    private String pdfaflavour;
    CustomRuleEvalutaion customRuleEvalInstance;

    public CustomResponse() {
    }

    public CustomResponse(String compliant, String pdfaflavour, CustomRuleEvalutaion customRuleEvalInstance) {
        this.compliant = compliant;
        this.pdfaflavour = pdfaflavour;
        this.customRuleEvalInstance = customRuleEvalInstance;
    }

    public String toJsonString() throws JsonProcessingException {
        customRuleEvalInstance.performDifferenceRuleViolation();
        compliant = customRuleEvalInstance.getCompliant();
        return new ObjectMapper().writeValueAsString(customRuleEvalInstance);
    }
}

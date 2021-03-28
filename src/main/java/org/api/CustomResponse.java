package org.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    //explanation of @JsonIgnore
    //https://www.baeldung.com/jackson-annotations
    @JsonIgnore
    private String pdfaflavour;
    @JsonIgnore
    private ArrayList<String> ruleValidationExceptions;

    public CustomResponse() {
    }

    public CustomResponse(String compliant, String pdfaflavour, ArrayList<String> ruleValidationExceptions) {
        this.compliant = compliant;
        this.pdfaflavour = pdfaflavour;
        this.ruleValidationExceptions = ruleValidationExceptions;
    }

    public void differenceRuleValidationExceptons(ArrayList<String> setOfExceptionsFromFile) {
        ruleValidationExceptions.removeAll(setOfExceptionsFromFile);
    }
}

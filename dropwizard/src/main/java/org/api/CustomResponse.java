package org.api;

import java.util.List;

public class CustomResponse {
    private String compliant;
    private String pdfaflavour;
    private List<String> listRuleViolationClause;

    public CustomResponse() {
    }

    public CustomResponse(String compliant, String pdfaflavour, List<String> Clause) {
        this.compliant = compliant;
        this.pdfaflavour = pdfaflavour;
        this.listRuleViolationClause = Clause;
    }

    public void setListRuleViolationClause(List<String> Clause) {
        this.listRuleViolationClause = Clause;
    }

    public String getCompliant() {
        return compliant;
    }

    public void setCompliant(String compliant) {
        this.compliant = compliant;
    }

    public String getPdfaflavour() {
        return pdfaflavour;
    }

    public void setPdfaflavour(String pdfaflavour) {
        this.pdfaflavour = pdfaflavour;
    }


    public List<String> getListRuleViolationClause() {
        return listRuleViolationClause;
    }

}

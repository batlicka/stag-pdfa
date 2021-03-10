package org;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;

public class DropwizardConfiguration extends Configuration {
    // TODO: implement service configuration
    @NotNull
    private String urlToVeraPDFrest;

    @NotNull
    private String pathToRuleViolationExceptionFile;

    @NotNull
    private String pathToSentFilesFolder;

    @JsonProperty
    public String getUrlToVeraPDFrest() {
        return urlToVeraPDFrest;
    }
    @JsonProperty
    public void setUrlToVeraPDFrest(String urlToVeraPDFrest) {
        this.urlToVeraPDFrest = urlToVeraPDFrest;
    }


    public String getPathToRuleViolationExceptionFile() {
        return pathToRuleViolationExceptionFile;
    }

    public void setPathToRuleViolationExceptionFile(String pathToRuleViolationExceptionFile) {
        this.pathToRuleViolationExceptionFile = pathToRuleViolationExceptionFile;
    }

    public String getPathToSentFilesFolder() {
        return pathToSentFilesFolder;
    }

    public void setPathToSentFilesFolder(String pathToSentFilesFolder) {
        this.pathToSentFilesFolder = pathToSentFilesFolder;
    }
}

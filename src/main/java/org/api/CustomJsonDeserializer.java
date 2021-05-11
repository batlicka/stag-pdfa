package org.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class CustomJsonDeserializer {


    private JsonNode rootNode;

    public CustomJsonDeserializer(JsonNode rootNode) {
        this.rootNode = rootNode;
    }

    public String getAttributeValueFromRoot(String attribute) {
        try {
            return rootNode.get(attribute).asText();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public ArrayList<String> getViolatedRules() {
        ArrayList<String> testAsserrion = new ArrayList<String>();
        ArrayNode arrayNode = (ArrayNode) rootNode.at("/testAssertions");

        if (arrayNode.isEmpty()) {
            return null;
        } else {
            JsonNode arrayElement;
            for (int i = 0; i < arrayNode.size(); i++) {
                arrayElement = arrayNode.get(i).at("/ruleId");
                testAsserrion.add(arrayElement.get("clause").asText() + "-" + arrayElement.get("testNumber").asText());
            }
            return testAsserrion;
        }

    }


}

package org.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CustomJsonFileDeserializer {
    private File file;

    public CustomJsonFileDeserializer(File file) {
        this.file = file;
    }

    //constructor consumes name of JSON file located in wirking directory
    //If you want to assure where is you working directory, use: System.getProperty("user.dir")


    public ArrayList<String> deserializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        //File file = new File("RuleViolationException.json");
        try {
            JsonNode rootNode2 = objectMapper.readTree(this.file);
            ArrayList<String> arr = new ArrayList<String>();
            ArrayNode arrayNode = (ArrayNode) rootNode2.at("/RuleViolationExceptions");

            for (int i = 0; i < arrayNode.size(); i++) {
                arr.add(arrayNode.get(i).asText());
            }
            return arr;
        } catch (JsonProcessingException e1) {
            System.out.println(e1.getMessage());
            return null;
        } catch (IOException e2) {
            System.out.println(e2.getMessage());
            return null;
        }
    }
}

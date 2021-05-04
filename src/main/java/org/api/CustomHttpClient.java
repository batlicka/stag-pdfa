package org.api;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class CustomHttpClient {
    @Getter
    @Setter
    private String errorMessage = "";
    @Getter
    private String responseMessage = "";
    private String urlToVeraPDFrest;
    private HttpPost httpPost;
    @Getter
    private StopWatch verapdf_rest_request_time;
    private CloseableHttpClient client;
    private CloseableHttpResponse response;
    @Getter
    private Integer statusCode = 0;
    @Getter
    private String vera_pdf_rest_response = "";

    public CustomHttpClient(String urlToVeraPDFrest) {
        this.urlToVeraPDFrest = urlToVeraPDFrest;
        //time of processing on veraPdf-rest
        verapdf_rest_request_time = new StopWatch();
    }

    public void createRequest(String testSwitch, InputStream inputStreamFromClass) {
        client = HttpClients.createDefault();
        httpPost = new HttpPost(urlToVeraPDFrest);

        //only for testing pursouses
        if (testSwitch.equals("f6")) {
            //header is not set veraPDF-rest return HTML
            httpPost.setHeader("Accept", "text/html");
        } else {
            httpPost.setHeader("Accept", "application/json");
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", inputStreamFromClass);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
    }

    public void sendRequest() throws IOException {
        //before execute client, start timer2, It will measure how long veraPDF-rest processing request.
        verapdf_rest_request_time.start();
        response = client.execute(httpPost);
        verapdf_rest_request_time.stop();
    }

    public void processResponse(ArrayList<String> ruleViolationExceptions) throws IOException {
        statusCode = response.getStatusLine().getStatusCode();
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getProtocolVersion());
        System.out.println(response.getStatusLine().getReasonPhrase());

        String responseString = new BasicResponseHandler().handleResponse(response);
        //System.out.println(responseString);
        /*FileOutputStream outputStream = new FileOutputStream("D:\\tmp\\stag-pdfa_out.txt");
        byte[] strToBytes = responseString.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();*/

        //https://stackoverflow.com/questions/9077933/how-to-find-http-media-type-mime-type-from-response
        HttpEntity entity = response.getEntity();
        ContentType contentType;
        String mimeType = "";
        if (entity != null) {
            contentType = ContentType.get(entity);
            mimeType = contentType.getMimeType();
        }

        // parse JSON if response is in format application/json
        if (mimeType.equalsIgnoreCase("application/json")) {
            System.out.println("From veraPDF-rest came response in Content-type: application/json");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseString);

            CustomJsonDeserializer des = new CustomJsonDeserializer(rootNode, ruleViolationExceptions.get(0).contains("-"));
            CustomRuleEvalutaion customRuleEvalInstance = new CustomRuleEvalutaion(ruleViolationExceptions, des.getViolatedRules(), des.getAttributeValueFromRoot("compliant"));
            CustomResponse responseCurrent = new CustomResponse(
                    des.getAttributeValueFromRoot("compliant"),
                    des.getAttributeValueFromRoot("pdfaflavour"),
                    customRuleEvalInstance
            );

            vera_pdf_rest_response = responseCurrent.getCompliant();
            responseMessage = responseCurrent.response();

            //only for testing purpouse
            System.out.println("result after difference of sets: ");
            System.out.println("|Compliant: " + responseCurrent.getCompliant() + "|pdfaflavour: " + responseCurrent.getPdfaflavour());
            System.out.println("List of Clauses: " + customRuleEvalInstance.getRuleViolation());
            System.out.println("responseMessage: ");
            System.out.println(responseMessage);
        } else {
            //from veraPdf-rest was returned response in different Content-type than "application/json"
            errorMessage = "{\"Response from veraPDF wasn't in Content-type: application/json \"}";
            //only for testing purpouses
            System.out.println(errorMessage);
        }
    }

}

package org.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import org.api.*;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Path("/api")
public final class ApiResource {
    private static ArrayList<String> ruleViolationExceptions = new ArrayList<String>();
    private static String urlToVeraPDFrest;
    private static String pathToSentFilesFolder;
    private static SQLite databaseInstance;
    private static LinkedHashMap<String, List<String>> stagpdfa;
    private static String delayProcessingTheRequest;
    private static String testSwitch;
    private static String inputStreamProcessor;
    private static ArrayList<String> emailPropertisies;

    public ApiResource(Map stagpdfa) {
        //https://stackoverflow.com/questions/49771099/how-to-get-string-from-config-yml-file-in-dropwizard-resource
        //https://stackoverflow.com/questions/13581997/how-get-value-from-linkedhashmap-based-on-index-not-on-key?answertab=votes#tab-top

        this.stagpdfa = new LinkedHashMap<String, List<String>>(stagpdfa);
        ruleViolationExceptions = new ArrayList<String>(this.stagpdfa.get("exceptions"));
        this.pathToSentFilesFolder = this.stagpdfa.get("pathToSentFilesFolder").get(0);
        this.urlToVeraPDFrest = this.stagpdfa.get("urlToVeraPDFrest").get(0);
        this.databaseInstance = new SQLite(this.stagpdfa.get("databaseUrlJdbc").get(0), this.stagpdfa.get("cleanDatabaseTableAtStart").get(0));
        this.delayProcessingTheRequest = this.stagpdfa.get("delayProcessingTheRequest").get(0);
        this.testSwitch = this.stagpdfa.get("testSwitch").get(0);
        this.inputStreamProcessor = this.stagpdfa.get("inputStramProcessor").get(0);
        this.emailPropertisies = new ArrayList<String>(this.stagpdfa.get("javaMail"));
    }

    @GET
    @Path("/{PathParam}/test2")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConstant(@PathParam("PathParam") String PathParam) {
        return "This is your PathParameter: " + PathParam;
    }

    @GET
    @Path("/ok")
    @Produces(MediaType.APPLICATION_JSON)//APPLICATION_JSON
    public Response getOkResponse() {

        //https://www.baeldung.com/jax-rs-response
        String message = "{\"hello\": \"This is a JSON response\"}";

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/validate/{profileId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON})
    public static Response safePdf(@PathParam("profileId") String profileId,
                                   @FormDataParam("sha1Hex") String sha1Hex,
                                   @FormDataParam("file") InputStream uploadedInputStream) {

        return resendAndValidate(uploadedInputStream);
    }

    public static Response resendAndValidate(InputStream uploadedInputStream) {
        //time of processing on stag-pdfa
        StopWatch request_time = StopWatch.createStarted();
        //time of processing on veraPdf-rest
        StopWatch verapdf_rest_request_time = new StopWatch();

        //for purpouse of testing,
        if (delayProcessingTheRequest.equals("true")) {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException inter) {
                inter.getStackTrace();
            }
        }

        String responseMessage = "";
        String nameForPdf = "";
        String vera_pdf_rest_response = "";
        String errorMessage = "";
        //default value of status code is 0, during running of program it is set on proper value
        Integer statusCode = 0;
        InputStream inputStreamFromClass;
        Email email = new Email(emailPropertisies);

        try {
            if (inputStreamProcessor.equals("oldInputStreamProcessor")) {
                OldInputStreamProcessor oldispInstance = new OldInputStreamProcessor(pathToSentFilesFolder);
                nameForPdf = oldispInstance.saveFileAndCalculateSHA1(uploadedInputStream);
                //load input stream from bytesArray
                inputStreamFromClass = oldispInstance.createInputStreamFrombytesArrayuploadedInputStream();
            } else if (inputStreamProcessor.equals("InputStreamProcessor")) {
                InputStreamProcessor ispInstance = new InputStreamProcessor(pathToSentFilesFolder);
                nameForPdf = ispInstance.saveFileAndClculateSHA1(uploadedInputStream);
                //load input stream from file
                inputStreamFromClass = ispInstance.createInputStreamFromFile();
            } else {
                //switch off saveing processed file on disk and calculating sha1
                inputStreamFromClass = uploadedInputStream;
            }

            //create log to logging table, are logged: nameForPDF and Timestamp(logged automatically)
            databaseInstance.insertStagpdfaLogs(nameForPdf);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(urlToVeraPDFrest);

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
            //podívat se zda metoda build streamuje přímo, nebo blokuje
            httpPost.setEntity(multipart);

            //before execute client, start timer2, It will measure how long veraPDF-rest processing request.
            verapdf_rest_request_time.start();
            CloseableHttpResponse response = client.execute(httpPost);
            verapdf_rest_request_time.stop();

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
                CustomJsonDeserializer des = new CustomJsonDeserializer(rootNode);
                CustomRuleEvalutaion customRuleEvalInstance = new CustomRuleEvalutaion(ruleViolationExceptions, des.getViolatedRules(), des.getAttributeValueFromRoot("compliant"));
                CustomResponse responseCurrent = new CustomResponse(
                        des.getAttributeValueFromRoot("compliant"),
                        des.getAttributeValueFromRoot("pdfaflavour"),
                        customRuleEvalInstance
                );
                //logování veraPDF-rest compliant to SQLite database with logs
                vera_pdf_rest_response = responseCurrent.getCompliant();

                //decision logic agreed on google docs
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
        } catch (UnrecognizedPropertyException e1) {
            errorMessage = ExceptionUtils.getStackTrace(e1);
            statusCode = 500;
            System.out.println(errorMessage);
        } catch (JsonMappingException e2) {
            errorMessage = ExceptionUtils.getStackTrace(e2);
            statusCode = 500;
            System.out.println(errorMessage);
        } catch (JsonParseException e3) {
            System.out.println("response can't be processed, because response is not in JSON format");
            statusCode = 500;
        } catch (JsonProcessingException e4) {
            errorMessage = ExceptionUtils.getStackTrace(e4);
            statusCode = 500;
            System.out.println(errorMessage);
        } catch (ClientProtocolException e5) {
            errorMessage = ExceptionUtils.getStackTrace(e5);
            statusCode = 500;
            System.out.println(errorMessage);
        } catch (IOException e6) {
            errorMessage = ExceptionUtils.getStackTrace(e6);
            statusCode = 500;
            System.out.println(errorMessage);
        } catch (NoSuchAlgorithmException e7) {
            errorMessage = ExceptionUtils.getStackTrace(e7);
            statusCode = 500;
            System.out.println(errorMessage);
        } catch (SecurityException e8) {
            errorMessage = ExceptionUtils.getStackTrace(e8);
            statusCode = 500;
            System.out.println(errorMessage);
        } catch (NullPointerException e9) {
            errorMessage = ExceptionUtils.getStackTrace(e9);
            statusCode = 500;
            System.out.println(errorMessage);
        }
        request_time.stop();

        //https://docs.oracle.com/cd/E19830-01/819-4721/beajw/index.html
        databaseInstance.updateStagpdfaLogs(vera_pdf_rest_response, (int) request_time.getTime(TimeUnit.MILLISECONDS), (int) verapdf_rest_request_time.getTime(TimeUnit.MILLISECONDS), statusCode, errorMessage, nameForPdf);

        Response response;
        if (errorMessage.isEmpty()) {
            response = Response
                    .status(Response.Status.OK)
                    .entity(responseMessage)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else {
            email.sendEamil(errorMessage);
            response = Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"Error 500 Internal Server Error\"} ")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        //only for testing purpouses
        System.out.println("response Message returned to IS stag: ");
        System.out.println(responseMessage);
        response = setFaultyResponseMessage(email, response);

        return response;
    }

    public static Response setFaultyResponseMessage(Email email, Response response) {
        String testResponseMessage = "";
        if (testSwitch.equals("f5") || testSwitch.equals("f32") || testSwitch.equals("f4")) {
            if (testSwitch.equals("f5")) {
                testResponseMessage = "{\"compliant\": \"Response from veraPDF wasn't in Content-type: application/json \"}";
                email.sendEamil(testResponseMessage);
            } else if (testSwitch.equals("f32")) {
                testResponseMessage = "{\"Response from veraPDF wasn't in Content-type: application/json \"}";
                email.sendEamil(testResponseMessage);
            } else if (testSwitch.equals("f4")) {
                testResponseMessage = "{\"klic\": \"Response from veraPDF wasn't in Content-type: application/json \"}";
                email.sendEamil(testResponseMessage);
            }
            response = Response
                    .status(Response.Status.OK)
                    .entity(testResponseMessage)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else if (testSwitch.equals("f31")) {
            System.out.println("response Message returned to IS stag: ");
            testResponseMessage = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" /></head><body><h2>This is test response in html</h2></body></html>";
            System.out.println(testResponseMessage);
            email.sendEamil(testResponseMessage);
            response = Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_HTML)
                    .entity(testResponseMessage)
                    .build();
        }
        return response;
    }
}

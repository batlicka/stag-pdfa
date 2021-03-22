package org.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
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

import org.api.CustomJsonDeserializer;
import org.api.CustomJsonFileDeserializer;
import org.api.CustomResponse;
import org.api.SQLite;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Path("/api")
public final class ApiResouce {
    private static final String SHA1_NAME = "SHA-1";
    private static ArrayList<String> RuleViolationException=new ArrayList<String>();
    private static String urlToVeraPDFrest;
    private static String pathToSentFilesFolder;
    private static SQLite databaseInstance;
    private static LinkedHashMap<String, List<String>> stagpdfa;
    private static String delayProcessingTheRequest;

    private static String testSwitch;

    public ApiResouce( Map stagpdfa){
        //https://stackoverflow.com/questions/49771099/how-to-get-string-from-config-yml-file-in-dropwizard-resource
        //https://stackoverflow.com/questions/13581997/how-get-value-from-linkedhashmap-based-on-index-not-on-key?answertab=votes#tab-top

        //SQLite databaseInstance = new SQLite(configuration.getStagpdfa().get("configuration.getStagpdfa()").get(0));
        this.stagpdfa= new LinkedHashMap<String, List<String>>(stagpdfa);
        RuleViolationException = new ArrayList<String>(this.stagpdfa.get("exceptions"));
        this.pathToSentFilesFolder=this.stagpdfa.get("pathToSentFilesFolder").get(0);
        this.urlToVeraPDFrest=this.stagpdfa.get("urlToVeraPDFrest").get(0);
        this.databaseInstance = new SQLite(this.stagpdfa.get("databaseUrlJdbc").get(0), this.stagpdfa.get("cleanDatabaseTableAtStart").get(0));
        this.delayProcessingTheRequest = this.stagpdfa.get("delayProcessingTheRequest").get(0);
        this.testSwitch = this.stagpdfa.get("testSwitch").get(0);
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConstant(@QueryParam("var") Optional<String> var) {

        String initialString = "Hello World!";

        File out = new File("out.pdf");
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        try {
            out.createNewFile();
            OutputStream fos = new FileOutputStream(out);

            InputStream uploadedInputStreamV = IOUtils.toInputStream(initialString, "UTF-8");

            IOUtils.copy(uploadedInputStreamV, fos);
            fos.close();
            uploadedInputStreamV.close();
            // If there wasn't a file there beforehand, there is one now.
        } catch (IOException e) {

            // If there was, no harm, no foul
        }
        return "ahoj: " + var.toString();

    }

    @GET
    @Path("/{PathParam}/test2")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConstant(@PathParam("PathParam") String PathParam) {
        return "This is your PathParameter: " + PathParam;
    }

    @GET
    @Path("/desJSON")
    @Produces(MediaType.TEXT_PLAIN)
    public String desJson(@QueryParam("var") Optional<String> var) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File("RuleViolationException.json");
        JsonNode rootNode = objectMapper.readTree(file);

        ArrayList<String> arr = new ArrayList<String>();

        ArrayNode arrayNode = (ArrayNode) rootNode.at("/RuleViolationExceptions");
        JsonNode arrayElement;
        for (int i = 0; i < arrayNode.size(); i++) {
            arrayElement = arrayNode.get(i).at("/ruleId");
            arr.add(arrayElement.get("clause").asText());
        }

        //List<String> valException = objectMapper.readValue(file, new TypeReference<List<String>>(){ });
        return "desJSON";
    }

    @GET
    @Path("/ok")
    @Produces(MediaType.APPLICATION_JSON)//APPLICATION_JSON
    public Response getOkResponse() {



        //https://www.baeldung.com/jax-rs-response
        String message = "{\"hello\": \"This is a JSON response\"}";

        return Response
                .status(Response.Status.OK)
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();

        //https://www.baeldung.com/jax-rs-response
    }

    @POST
    @Path("/calsha")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public static String calculateSHA(@FormDataParam("file") InputStream uploadedInputStream) throws NoSuchAlgorithmException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(uploadedInputStream, baos);
        byte[] bytesArrayuploadedInputStream = baos.toByteArray();
        //clone
        InputStream firstCloneUploadedInputStream = new ByteArrayInputStream(bytesArrayuploadedInputStream);
        InputStream secondCloneUploadedInputStream = new ByteArrayInputStream(bytesArrayuploadedInputStream);

        String sha1Hex = org.apache.commons.codec.digest.DigestUtils.sha1Hex(firstCloneUploadedInputStream);
        System.out.println("sha1 calculated from uploadedInputStream, method1: " + sha1Hex);

        String value = "this is a test";

        String sha1 = "";

        // With the java libraries
        //https://www.baeldung.com/convert-input-stream-to-array-of-bytes
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            //digest.update(value.getBytes("utf8"));
            digest.update(bytesArrayuploadedInputStream);
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
            System.out.println("sha1 calculated from uploadedInputStream, method2: " + sha1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("The sha1 of \"" + value + "\" is:");
        System.out.println(sha1);
        System.out.println();

        MessageDigest sha11 = MessageDigest.getInstance(SHA1_NAME);
        //DigestInputStream dis = new DigestInputStream(uploadedInputStream, sha1);
        if (sha1Hex.equalsIgnoreCase(Hex.encodeHexString(sha11.digest()))) {
            System.out.println("sha1 are same");
        }
        return "this is calculated sha1";
    }

    @POST
    @Path("/validate/{profileId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON})
    public static String safePdf(@PathParam("profileId") String profileId,
                                 @FormDataParam("sha1Hex") String sha1Hex,
                                 @FormDataParam("file") InputStream uploadedInputStream) {
        //time of processing on stag-pdfa
        StopWatch request_time = StopWatch.createStarted();
        //time of processing on veraPdf-rest
        StopWatch verapdf_rest_request_time = new StopWatch();

        //for purpouse of testing,
        if(delayProcessingTheRequest.equals("true")){
            try {
                Thread.sleep(6000);
            }catch (InterruptedException inter){
                inter.getStackTrace();
            }
        }

        String responseMessage="";
        String nameForPdf="";
        String vera_pdf_rest_response="";
        String errorMessage="";
        try {
            //https://stackoverflow.com/questions/5923817/how-to-clone-an-inputstream
            //saveing of uploadedInputStream to pdf in local folder
            //create byte array from accepted uploadedInputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(uploadedInputStream, baos);
            byte[] bytesArrayuploadedInputStream = baos.toByteArray();

            //calculate sha1 from uploadedInputStream and create pdf file with it's sha1 name
            nameForPdf =calculateSha1Hex(bytesArrayuploadedInputStream);
            String fullPathIncludedPdfName=pathToSentFilesFolder+nameForPdf+".pdf";
            File output = new File(fullPathIncludedPdfName);
            FileOutputStream out =new FileOutputStream(output);

            //clone of input stream for building POST
            InputStream firstCloneUploadedInputStream = new ByteArrayInputStream(bytesArrayuploadedInputStream);
            out.write(bytesArrayuploadedInputStream);
            out.close();


            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(urlToVeraPDFrest);
            //http://localhost:9090/api/validate/auto
            //http://pdfa.k.utb.cz:8080/api/validate/auto

            //only for testing pursouses
            if(testSwitch.equals("f3a")){
                //header is not set veraPDF-rest return HTML
            }
            else{
                httpPost.setHeader("Accept", "application/json");
            }
            //usualy " httpPost.setHeader("Accept", "application/json");" without if

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", firstCloneUploadedInputStream);
            //builder.addBinaryBody("sha1Hex",IOUtils.toInputStream("e6393c003e014acaa8e6f342ae8f86a4e2e8f7bf", "UTF-8"));
            HttpEntity multipart = builder.build();
            //podívat se zda metoda build streamuje přímo, nebo blokuje
            httpPost.setEntity(multipart);

            //before execute client, start timer2, It will measure how long veraPDF-rest processing request.
            verapdf_rest_request_time.start();
            CloseableHttpResponse response = client.execute(httpPost);
            //after obtaining response from veraPDF-rest stop stopwatch2
            verapdf_rest_request_time.stop();

            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine().getProtocolVersion());
            System.out.println(response.getStatusLine().getReasonPhrase());

            String responseString = new BasicResponseHandler().handleResponse(response);
            //System.out.println(responseString);

            //https://stackoverflow.com/questions/9077933/how-to-find-http-media-type-mime-type-from-response
            HttpEntity entity = response.getEntity();
            ContentType contentType;
            String mimeType="";
            if (entity != null){
                contentType = ContentType.get(entity);
                mimeType= contentType.getMimeType();
            }

            // parse JSON if response is in format application/json
            if(mimeType.equalsIgnoreCase("application/json")) {
                System.out.println("From veraPDF-rest came response in Content-type: application/json");

                ObjectMapper mapper = new ObjectMapper();
                System.out.println("1");
                JsonNode rootNode = mapper.readTree(responseString);
                System.out.println("2");
                CustomJsonDeserializer des = new CustomJsonDeserializer(rootNode);
                System.out.println("3");
                CustomResponse responseCurrent = new CustomResponse(
                        des.getAttributeValueFromRoot("compliant"),
                        des.getAttributeValueFromRoot("pdfaflavour"),
                        des.getTestAssertionsArray()
                );
                //logování veraPDF-rest compliant to SQLite database with logs
                vera_pdf_rest_response = responseCurrent.getCompliant();

                //rest api rozhodne, jak se výjimka ošetří
                //na zobrazování chyb použít běžné http kody a chybu specifikovat v jeho správě

                System.out.println("4");
                //decision logic agreed on google docs
                if (responseCurrent.getCompliant().equalsIgnoreCase("true")) {
                    System.out.println("5.1");
                    responseMessage = new ObjectMapper().writeValueAsString(responseCurrent);
                } else {
                    System.out.println("6.1");
                    responseCurrent.differenceRuleValidationExceptons(RuleViolationException);
                    System.out.println("6.2");
                    if (responseCurrent.getRuleValidationExceptions().isEmpty()) {
                        System.out.println("7.1");
                        responseCurrent.setCompliant("true");
                        System.out.println("7.2");
                        responseMessage = new ObjectMapper().writeValueAsString(responseCurrent);
                        System.out.println("7.3");
                    } else {
                        responseMessage = new ObjectMapper().writeValueAsString(responseCurrent);
                        System.out.println("8.1");
                    }
                }
                //only for testing purpouse
                System.out.println("result after difference of sets: ");
                System.out.println("|Compliant: " + responseCurrent.getCompliant() + "|pdfaflavour: " + responseCurrent.getPdfaflavour());
                System.out.println("List of Clauses: " + responseCurrent.getRuleValidationExceptions());
                System.out.println("responseMessage: ");
                System.out.println(responseMessage);

                //throw new ClientProtocolException();
            }else{
                //from veraPdf-rest was returned response in different Content-type than "application/json"
                responseMessage = "{\"Response from veraPDF wasn't in Content-type: application/json \"}";
            }


        } catch (UnrecognizedPropertyException e1) {
            System.out.println(ExceptionUtils.getStackTrace(e1));
        } catch (JsonMappingException e2) {
            System.out.println(ExceptionUtils.getStackTrace(e2));
        } catch (JsonParseException e3) {
            System.out.println("response can't be processed, because response is not in JSON format");
        } catch (JsonProcessingException e4) {
            System.out.println(ExceptionUtils.getStackTrace(e4));
        } catch (ClientProtocolException e5) {
            System.out.println(ExceptionUtils.getStackTrace(e5));
        } catch (IOException e6) {
            System.out.println(ExceptionUtils.getStackTrace(e6));
        }
        request_time.stop();

        //https://docs.oracle.com/cd/E19830-01/819-4721/beajw/index.html
        databaseInstance.insertStagpdfaLogs(nameForPdf,vera_pdf_rest_response, (int)request_time.getTime(TimeUnit.MILLISECONDS), (int)verapdf_rest_request_time.getTime(TimeUnit.MILLISECONDS) );
        databaseInstance.printSQLContentOnConsole();

        //only for testing purpouses
        if(testSwitch.equals("fa")){
            responseMessage="{\"compliant\": \"Response from veraPDF wasn't in Content-type: application/json \"}";
        }else if(testSwitch.equals("fb")){
            responseMessage="{\"Response from veraPDF wasn't in Content-type: application/json \"}";
        }
        else{
            //return responseMessage in normal form
        }
        return responseMessage;
    }

    public static String calculateSha1Hex(byte[] bytesArrayuploadedInputStream){
        // With the java libraries
        //https://www.baeldung.com/convert-input-stream-to-array-of-bytes
        String Sha1Hex="";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            //digest.update(value.getBytes("utf8"));
            digest.update(bytesArrayuploadedInputStream);
            Sha1Hex = String.format("%040x", new BigInteger(1, digest.digest()));
            return Sha1Hex;
        } catch (Exception e) {
            e.getMessage();
            return Sha1Hex;
        }
    }
}

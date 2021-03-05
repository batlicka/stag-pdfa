package org.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
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
import org.api.RuleValidationException;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/api")
public final class
ApiResouce {
    private static final String SHA1_NAME = "SHA-1";
    private static CustomJsonFileDeserializer fileDes = new CustomJsonFileDeserializer(new File("RuleViolationException.json"));
    private static final ArrayList<String> RuleViolationException = fileDes.deserializer();

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

        String sha1Hex = org.apache.commons.codec.digest.DigestUtils.sha1Hex(uploadedInputStream);
        System.out.println("sha1 calculated from uploadedInputStream: " + sha1Hex);

        String value = "this is a test";

        String sha1 = "";

        // With the java libraries
        //https://www.baeldung.com/convert-input-stream-to-array-of-bytes
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            byte[] data = IOUtils.toByteArray(uploadedInputStream);
            //digest.update(value.getBytes("utf8"));
            digest.update(data);
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
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
    @Produces(MediaType.APPLICATION_JSON)
    public static String safePdf(@PathParam("profileId") String profileId,
                                 @FormDataParam("sha1Hex") String sha1Hex,
                                 @FormDataParam("file") InputStream uploadedInputStream) {
        System.out.println(String.format("accepted sha1Hex: %s", sha1Hex));

        try {
            String responseMessage="";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://pdfa.k.utb.cz:7070/api/validate/auto");
            //http://localhost:9090/api/validate/auto
            //http://pdfa.k.utb.cz:8080/api/validate/auto

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", uploadedInputStream);
            //builder.addBinaryBody("sha1Hex",IOUtils.toInputStream("e6393c003e014acaa8e6f342ae8f86a4e2e8f7bf", "UTF-8"));
            HttpEntity multipart = builder.build();
            //podívat se zda metoda build streamuje přímo, nebo blokuje
            httpPost.setEntity(multipart);

            CloseableHttpResponse response = client.execute(httpPost);

            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine().getProtocolVersion());
            System.out.println(response.getStatusLine().getReasonPhrase());

            String responseString = new BasicResponseHandler().handleResponse(response);
            System.out.println(responseString);

            //https://stackoverflow.com/questions/9077933/how-to-find-http-media-type-mime-type-from-response
            HttpEntity entity = response.getEntity();
            ContentType contentType;
            String mimeType="";
            if (entity != null){
                contentType = ContentType.get(entity);
                mimeType= contentType.getMimeType();
            }

            if(mimeType.equalsIgnoreCase("application/json")) {
                // parse JSON
                ObjectMapper mapper = new ObjectMapper();

                JsonNode rootNode = mapper.readTree(responseString);

                CustomJsonDeserializer des = new CustomJsonDeserializer(rootNode);

                CustomResponse responseCurrent = new CustomResponse(
                        des.getAttributeValueFromRoot("compliant"),
                        des.getAttributeValueFromRoot("pdfaflavour"),
                        des.getTestAssertionsArray()
                );
                //rest api rozhodne, jak se výjimka ošetří
                //na zobrazování chyb použít běžné http kody a chybu specifikovat v jeho správě

                System.out.println("|Compliant: " + responseCurrent.getCompliant() + "|pdfaflavour: " + responseCurrent.getPdfaflavour());
                System.out.println("List of Clauses: " + responseCurrent.getRuleValidationExceptions());

                //decision logic agreed on google docs
                if (responseCurrent.getCompliant().equalsIgnoreCase("true")) {
                    responseMessage = new ObjectMapper().writeValueAsString(responseCurrent);
                    return responseMessage;
                } else {
                    responseCurrent.intersectionRuleValidationExceptons(RuleViolationException);
                    if (responseCurrent.getRuleValidationExceptions().isEmpty()) {
                        responseCurrent.setCompliant("true");
                        responseMessage = new ObjectMapper().writeValueAsString(responseCurrent);
                        return responseMessage;
                    } else {
                        responseMessage = new ObjectMapper().writeValueAsString(responseCurrent);
                        return responseMessage;
                    }
                }
            }else{
                //from veraPdf-rest was returned response in different Content-type than "application/json"
                return "{\"Response from veraPDF wasn't in Content-type: application/json \"}";
            }


        } catch (UnrecognizedPropertyException e1) {
            System.out.println(e1.getMessage());
        } catch (JsonMappingException e2) {
            System.out.println(e2.getMessage());
        } catch (JsonParseException e3) {
            System.out.println("response can't be processed, because response is not in JSON format");
        } catch (JsonProcessingException e4) {
            System.out.println(e4.getMessage());
        } catch (ClientProtocolException e5) {
            System.out.println(e5.getMessage());
        } catch (IOException e6) {
            System.out.println(e6.getMessage());
        }
        return "some error occured, error message: To Do...";

    }
}
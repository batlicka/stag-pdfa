package org.resources;



import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

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

import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.server.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.ext.RuntimeDelegate;

import java.io.*;
import java.util.Optional;

@Path("/api")
public final class ApiResouce {
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConstant(@QueryParam("var") Optional<String> var){

        String initialString = "Hello World!";

        File out = new File("out.pdf");
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        try{
            out.createNewFile();
            OutputStream fos = new FileOutputStream(out);

            InputStream uploadedInputStreamV = IOUtils.toInputStream(initialString, "UTF-8");

            IOUtils.copy(uploadedInputStreamV,fos);
            fos.close();
            uploadedInputStreamV.close();
            // If there wasn't a file there beforehand, there is one now.
        } catch(IOException e){

            // If there was, no harm, no foul
        }
        //////////////////
        //vyzkoušej zkopírovat input strem z jednoho do druhého a potom uloži do souboru
        //podívej se na možnosti jak může být vložen soubor do POST requestu v projektu ApacheHTTTPClientStagPdf
        //////////////////
        return "ahoj: "+ var.toString();

    }

    /*@GET
    @Path("/{PathParam}/test2")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConstant(@PathParam("PathParam") String PathParam){
        return "This is your PathParameter: "+ PathParam;


    }*/

    @GET
    @Path("/ok")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOkResponse(){


        return  "{\"hello\": \"This is a JSON response\"}";

    }


    @POST
    @Path("/{profileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public static String safePdf(@PathParam("profileId") String profileId,
                          @FormDataParam("sha1Hex") String sha1Hex,
                          @FormDataParam("file") InputStream uploadedInputStream) {

        /*try{
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://pdfa.k.utb.cz:8080/api/validate/auto");//http://pdfa.k.utb.cz:8080/api/validate/auto
        //http://localhost:8080/api/validate/auto
        //http://pdfa.k.utb.cz:8080/api/validate/auto

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", uploadedInputStream);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        CloseableHttpResponse response = client.execute(httpPost);

            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine().getProtocolVersion());
            System.out.println(response.getStatusLine().getReasonPhrase());

        String responseString = new BasicResponseHandler().handleResponse(response);
        System.out.println(responseString);


        // parse JSON
        ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(responseString);

            CustomJsonDeserializer des = new CustomJsonDeserializer(rootNode);

            Response responseCurrent=new Response(
                    des.getAttributeValueFromRoot("compliant"),
                    des.getAttributeValueFromRoot("pdfaflavour"),
                    des.getClauseArray()
            );
            //rest api rozhodne, jak se výjimka ošetří
            //na zobrazování chyb použít běžné http kody a chybu specifikovat v jeho správě

            System.out.println("|Compliant: " + responseCurrent.getCompliant() +"|pdfaflavour: "+responseCurrent.getPdfaflavour());
            System.out.println("List of Clauses: " + responseCurrent.getListRuleViolationClause());

        }catch(UnrecognizedPropertyException e1){
            System.out.println(e1.getMessage());
        }catch(JsonMappingException e2){
            System.out.println(e2.getMessage());
        }catch (JsonParseException e3){
            System.out.println("response can't be processed, because response is not in JSON format");
        }catch(JsonProcessingException e4){
            System.out.println(e4.getMessage());
        }catch(ClientProtocolException e5){
            System.out.println(e5.getMessage());
        }catch (IOException e6){
            System.out.println(e6.getMessage());
        }

        return "end of ApacheHTTP client";*/
        return  "{\"hello\": \"This is a JSON response\"}";

    }
}

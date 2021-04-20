package org.api;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OldInputStreamProcessor {
    private String Sha1Hex = "";
    private String fullPathIncludedPdfName = "";
    private String pathToSentFilesFolder = "";
    private byte[] bytesArrayuploadedInputStream;
    private InputStream firstCloneUploadedInputStream;

    public OldInputStreamProcessor(String pathToSentFilesFolder) {
        this.pathToSentFilesFolder = pathToSentFilesFolder;
    }

    public String saveFileAndClculateSHA1(InputStream uploadedInputStream) throws IOException {
        //https://stackoverflow.com/questions/5923817/how-to-clone-an-inputstream
        //saveing of uploadedInputStream to pdf in local folder
        //create byte array from accepted uploadedInputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(uploadedInputStream, baos);
        bytesArrayuploadedInputStream = baos.toByteArray();
        /*//alternative ways of copying to byte array
        InputStream is;
        byte[] array = is.readAllBytes();
        byte[] bytes = IOUtils.toByteArray(is);
        \/
        //byte[] bytesArrayuploadedInputStream = IOUtils.toByteArray(uploadedInputStream);
        */

        //calculate sha1 from uploadedInputStream and create pdf file with it's sha1 name
        Sha1Hex = calculateSha1Hex(bytesArrayuploadedInputStream);
        String fullPathIncludedPdfName = pathToSentFilesFolder + Sha1Hex + ".pdf";
        File output = new File(fullPathIncludedPdfName);
        FileOutputStream out = new FileOutputStream(output);

        //save file
        InputStream firstCloneUploadedInputStream = new ByteArrayInputStream(bytesArrayuploadedInputStream);
        out.write(bytesArrayuploadedInputStream);
        out.close();

        return Sha1Hex;
    }

    public String calculateSha1Hex(byte[] bytesArrayuploadedInputStream) {
        // With the java libraries
        //https://www.baeldung.com/convert-input-stream-to-array-of-bytes
        String Sha1Hex = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            //digest.update(value.getBytes("utf8"));
            digest.update(bytesArrayuploadedInputStream);
            Sha1Hex = String.format("%040x", new BigInteger(1, digest.digest()));
            return Sha1Hex;
        } catch (Exception e) {
            e.getStackTrace();
            return Sha1Hex;
        }
    }

    public InputStream createInputStreamFrombytesArrayuploadedInputStream() {
        InputStream firstCloneUploadedInputStream = new ByteArrayInputStream(bytesArrayuploadedInputStream);
        return firstCloneUploadedInputStream;
    }
}

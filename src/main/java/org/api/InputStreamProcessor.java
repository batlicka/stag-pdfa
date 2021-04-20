package org.api;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.IO;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter
@Setter
public class InputStreamProcessor {
    private String Sha1Hex = "";
    private String fullPathIncludedPdfName = "";
    private String pathToSentFilesFolder = "";

    public InputStreamProcessor(String pathToSentFilesFolder) {
        this.pathToSentFilesFolder = pathToSentFilesFolder;
    }

    public String saveFileAndClculateSHA1(InputStream uploadedInputStream) throws NoSuchAlgorithmException, IOException {

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        String fullPathIncludedTempPdfName = pathToSentFilesFolder + "test" + ".pdf";

        File targetFile = new File(fullPathIncludedTempPdfName);
        OutputStream outputStream = new FileOutputStream(targetFile);
        //https://www.baeldung.com/convert-input-stream-to-a-file
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = uploadedInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            digest.update(buffer, 0, bytesRead);
        }
        IOUtils.closeQuietly(uploadedInputStream);
        IOUtils.closeQuietly(outputStream);
        Sha1Hex = String.format("%040x", new BigInteger(1, digest.digest()));

        //rename temp.pdf by Sha1Hex
        java.nio.file.Path source = java.nio.file.Paths.get(fullPathIncludedTempPdfName);
        fullPathIncludedPdfName = pathToSentFilesFolder + Sha1Hex + ".pdf";
        Files.move(source, source.resolveSibling(fullPathIncludedPdfName), StandardCopyOption.REPLACE_EXISTING);

        return Sha1Hex;
    }

    public InputStream createInputStreamFromFile() throws FileNotFoundException {
        //load input stream from file
        File savedFile = new File(fullPathIncludedPdfName);
        InputStream fileInputStream = new FileInputStream(savedFile);
        return fileInputStream;
    }
}

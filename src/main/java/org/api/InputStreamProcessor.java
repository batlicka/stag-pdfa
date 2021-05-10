package org.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public interface InputStreamProcessor {
    public String saveFileAndCalculateSHA1(InputStream uploadedInputStream) throws NoSuchAlgorithmException, IOException;

    public InputStream createInputStream() throws FileNotFoundException;
}

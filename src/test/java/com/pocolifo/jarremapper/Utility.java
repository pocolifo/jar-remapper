package com.pocolifo.jarremapper;

import java.io.File;
import java.net.URISyntaxException;

public class Utility {
    public static File getResourceAsFile(String resource) {
        try {
            return new File(ClassLoader.getSystemResource(resource).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}

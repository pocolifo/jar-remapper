package com.pocolifo.jarremapper.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.pocolifo.jarremapper.JarRemapper;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;

public class TestUtility {
    public static File getResourceAsFile(String resource) {
        try {
            return new File(ClassLoader.getSystemResource(resource).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void remap(JarMapping mapping, File input, AbstractRemappingEngine engine) throws IOException {
        JarRemapper.newRemap()
                .withRemappingEngine(engine)
                .withMappings(mapping)
                .withInputFile(input)
                .withOutputFile(new File("output.jar"))
                .overwriteOutputFile()
                .remap();
    }
}

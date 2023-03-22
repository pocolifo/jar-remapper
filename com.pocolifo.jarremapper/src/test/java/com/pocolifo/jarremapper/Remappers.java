package com.pocolifo.jarremapper;

import com.pocolifo.jarremapper.engine.standard.StandardRemappingEngine;
import com.pocolifo.jarremapper.mapping.JarMapping;

import java.io.File;
import java.io.IOException;

public class Remappers {
    public static final File OUTPUT = new File("output.jar");

    public static void remapNormal(File inputFile, JarMapping mapping) throws IOException {
        StandardRemappingEngine engine = new StandardRemappingEngine();
        engine.setInputFile(inputFile);
        engine.setOutputFile(OUTPUT);
        engine.setMapping(mapping);
        engine.remap();
    }

    public static void remapExcludeMetaInf(File inputFile, JarMapping mapping) throws IOException {
        StandardRemappingEngine engine = new StandardRemappingEngine();
        engine.excludeMetaInf();
        engine.setInputFile(inputFile);
        engine.setOutputFile(OUTPUT);
        engine.setMapping(mapping);
        engine.remap();
    }

    public static void remapNormalWithPlugin(File inputFile, JarMapping mapping) throws IOException {
        StandardRemappingEngine engine = new StandardRemappingEngine();
        engine.setRemappingPlugin(new SimpleProgressListener());
        engine.setInputFile(inputFile);
        engine.setOutputFile(OUTPUT);
        engine.setMapping(mapping);
        engine.remap();
    }

    public static void remapExcludeMetaInfWithPlugin(File inputFile, JarMapping mapping) throws IOException {
        StandardRemappingEngine engine = new StandardRemappingEngine();
        engine.excludeMetaInf();
        engine.setRemappingPlugin(new SimpleProgressListener());
        engine.setInputFile(inputFile);
        engine.setOutputFile(OUTPUT);
        engine.setMapping(mapping);
        engine.remap();
    }
}

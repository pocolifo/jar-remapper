package com.pocolifo.jarremapper.extensions;

import com.pocolifo.jarremapper.engine.standard.StandardRemappingEngine;
import com.pocolifo.jarremapper.mapping.JarMapping;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Remappers {
    public static final File OUTPUT = new File("output.jar");

    public static void remapSpecialSource(File inputFile, JarMapping mapping) throws IOException {
        SpecialSourceEngine engine = new SpecialSourceEngine();
        engine.setInputFile(inputFile);
        engine.setOutputFile(OUTPUT);
        engine.setMapping(mapping);
        engine.remap();
    }

    public static void remapTinyRemapper(File inputFile, JarMapping mapping) throws IOException {
        TinyRemapperEngine engine = new TinyRemapperEngine();
        engine.setInputFile(inputFile);
        engine.setOutputFile(OUTPUT);
        engine.setMapping(mapping);
        engine.remap();
    }

    public static void remapTinyRemapperExcludeMetaInf(File inputFile, JarMapping mapping) throws IOException {
        TinyRemapperEngine engine = new TinyRemapperEngine();
        engine.setInputFile(inputFile);
        engine.setOutputFile(OUTPUT);
        engine.setMapping(mapping);
        engine.excludeMetaInf();
        engine.remap();
    }
}

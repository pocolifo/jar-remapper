package com.pocolifo.jarremapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.pocolifo.jarremapper.engine.standard.StandardRemappingEngine;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;

public class JarRemapper {
    private AbstractRemappingEngine remappingEngine = new StandardRemappingEngine();
    private File inputFile;
    private File outputFile;
    private JarMapping mappings;
    private boolean overwriteOutputFile;

    public static JarRemapper newRemap() {
        return new JarRemapper();
    }

    public JarRemapper withRemappingEngine(AbstractRemappingEngine remappingEngine) {
        this.remappingEngine = remappingEngine;
        return this;
    }

    public JarRemapper withMappings(JarMapping mappings) {
        this.mappings = mappings;
        return this;
    }

    public JarRemapper withInputFile(File inputFile) {
        this.inputFile = inputFile;
        return this;
    }

    public JarRemapper withOutputFile(File outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public JarRemapper overwriteOutputFile() {
        this.overwriteOutputFile = !this.overwriteOutputFile;
        return this;
    }

    public void remap() throws IOException {
        assert this.inputFile != null;
        assert this.inputFile.exists();
        assert this.inputFile.isFile();

        assert this.outputFile != null;

        if (this.overwriteOutputFile) {
            Files.deleteIfExists(this.outputFile.toPath());
        }

        assert !this.outputFile.exists();

        assert this.mappings != null;

        this.remappingEngine.setInputFile(this.inputFile);
        this.remappingEngine.setOutputFile(this.outputFile);
        this.remappingEngine.setMapping(this.mappings);

        this.remappingEngine.remap();
    }
}

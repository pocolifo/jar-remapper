package com.pocolifo.jarremapper;

import com.pocolifo.jarremapper.reader.YarnMergedV2MappingReader;
import com.pocolifo.jarremapper.reader.YarnV1MappingReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.pocolifo.jarremapper.Utility.getResourceAsFile;

public class RemappingJava16Tests {
    @Test
    void yarnV1Remap() throws IOException {
        File output = new File("output.jar");

        Files.deleteIfExists(output.toPath());

        YarnV1MappingReader reader = new YarnV1MappingReader(
                getResourceAsFile("mappings/yarn/mappings-1.17.1.tiny")
        );

        JarRemapper.newBuilder()
                .setInputFile(getResourceAsFile("minecraft-1.17.1.jar"))
                .setJarMapping(reader.read())
                .setOutputFile(output)
                .setRemappingPlugin(new SimpleProgressListener())
                .excludeMetaInf()
                .remap();
    }

    @Test
    void yarnMergedV2Remap() throws IOException {
        File output = new File("output.jar");

        Files.deleteIfExists(output.toPath());

        YarnMergedV2MappingReader reader = new YarnMergedV2MappingReader(
                getResourceAsFile("mappings/yarnv2/mappings-merged-v2-1.17.1.tiny")
        );

        JarRemapper.newBuilder()
                .setInputFile(getResourceAsFile("minecraft-1.17.1.jar"))
                .setJarMapping(reader.read())
                .setOutputFile(output)
                .setRemappingPlugin(new SimpleProgressListener())
                .excludeMetaInf()
                .remap();
    }
}

package com.pocolifo.jarremapper;

import com.pocolifo.jarremapper.reader.McpMappingReader;
import com.pocolifo.jarremapper.reader.YarnMergedV2MappingReader;
import com.pocolifo.jarremapper.reader.YarnV1MappingReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.pocolifo.jarremapper.Utility.getResourceAsFile;

class RemappingJava8Tests {
    @Test
    public void mcpRemap() throws IOException {
        File output = new File("output.jar");

        Files.deleteIfExists(output.toPath());

        McpMappingReader reader = new McpMappingReader(
                getResourceAsFile("mappings/mcp/1.8.9/joined.srg"),
                getResourceAsFile("mappings/mcp/1.8.9/joined.exc"),

                getResourceAsFile("mappings/mcp/1.8.9/methods.csv"),
                getResourceAsFile("mappings/mcp/1.8.9/fields.csv"),
                getResourceAsFile("mappings/mcp/1.8.9/params.csv")
        );

        JarRemapper.newBuilder()
                .setInputFile(getResourceAsFile("minecraft-1.8.9.jar"))
                .setJarMapping(reader.read())
                .setOutputFile(output)
                .setRemappingPlugin(new SimpleProgressListener())
                .excludeMetaInf()
                .remap();
    }

    @Test
    void yarnV1Remap() throws IOException {
        File output = new File("output.jar");

        Files.deleteIfExists(output.toPath());

        YarnV1MappingReader reader = new YarnV1MappingReader(
                getResourceAsFile("mappings/yarn/mappings-1.8.9.tiny")
        );

        JarRemapper.newBuilder()
                .setInputFile(getResourceAsFile("minecraft-1.8.9.jar"))
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
                getResourceAsFile("mappings/yarnv2/mappings-merged-v2-1.8.9.tiny")
        );

        JarRemapper.newBuilder()
                .setInputFile(getResourceAsFile("minecraft-1.8.9.jar"))
                .setJarMapping(reader.read())
                .setOutputFile(output)
                .setRemappingPlugin(new SimpleProgressListener())
                .excludeMetaInf()
                .remap();
    }
}

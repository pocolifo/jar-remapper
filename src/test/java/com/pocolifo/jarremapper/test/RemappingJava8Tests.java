package com.pocolifo.jarremapper.test;

import com.pocolifo.jarremapper.reader.mcp.McpMappingReader;
import com.pocolifo.jarremapper.reader.tiny.Tiny1MappingReader;
import com.pocolifo.jarremapper.engine.standard.StandardRemappingEngine;
import com.pocolifo.jarremapper.reader.tiny.Tiny2MappingReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.pocolifo.jarremapper.test.TestUtility.getResourceAsFile;

class RemappingJava8Tests {
    @Test
    public void mcpRemap() throws IOException {
        McpMappingReader reader = new McpMappingReader(
                getResourceAsFile("mappings/mcp/1.8.9/joined.srg"),
                getResourceAsFile("mappings/mcp/1.8.9/joined.exc"),

                getResourceAsFile("mappings/mcp/1.8.9/methods.csv"),
                getResourceAsFile("mappings/mcp/1.8.9/fields.csv"),
                getResourceAsFile("mappings/mcp/1.8.9/params.csv")
        );

        TestUtility.remap(
                reader.read(),
                getResourceAsFile("minecraft-1.8.9.jar"),
                new StandardRemappingEngine().setRemappingPlugin(new SimpleProgressListener())
        );
    }

    @Test
    void yarnV1Remap() throws IOException {
        TestUtility.remap(
                new Tiny1MappingReader(getResourceAsFile("mappings/yarn/mappings-1.8.9.tiny")).read("official", "named"),
                getResourceAsFile("minecraft-1.8.9.jar"),
                new StandardRemappingEngine().setRemappingPlugin(new SimpleProgressListener())
        );
    }

    @Test
    void yarnMergedV2Remap() throws IOException {
        TestUtility.remap(
                new Tiny2MappingReader(getResourceAsFile("mappings/yarnv2/mappings-merged-v2-1.8.9.tiny")).read("official", "named"),
                getResourceAsFile("minecraft-1.8.9.jar"),
                new StandardRemappingEngine().setRemappingPlugin(new SimpleProgressListener())
        );
    }
}

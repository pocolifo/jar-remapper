package com.pocolifo.jarremapper.test;

import com.pocolifo.jarremapper.reader.tiny.Tiny1MappingReader;
import com.pocolifo.jarremapper.engine.standard.StandardRemappingEngine;
import com.pocolifo.jarremapper.reader.tiny.Tiny2MappingReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.pocolifo.jarremapper.test.TestUtility.getResourceAsFile;

public class RemappingJava16Tests {
    @Test
    void yarnV1Remap() throws IOException {
        TestUtility.remap(
                new Tiny1MappingReader(getResourceAsFile("mappings/yarn/mappings-1.17.1.tiny")).read("official", "named"),
                getResourceAsFile("minecraft-1.17.1.jar"),
                new StandardRemappingEngine().setRemappingPlugin(new SimpleProgressListener())
        );
    }

    @Test
    void yarnMergedV2Remap() throws IOException {
        TestUtility.remap(
                new Tiny2MappingReader(getResourceAsFile("mappings/yarnv2/mappings-merged-v2-1.17.1.tiny")).read("official", "named"),
                getResourceAsFile("minecraft-1.17.1.jar"),
                new StandardRemappingEngine().setRemappingPlugin(new SimpleProgressListener())
        );
    }
}

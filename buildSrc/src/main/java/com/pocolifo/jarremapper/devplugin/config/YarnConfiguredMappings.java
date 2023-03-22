package com.pocolifo.jarremapper.devplugin.config;

import com.pocolifo.jarremapper.devplugin.IOUtility;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class YarnConfiguredMappings extends AbstractConfiguredMapping {
    public final String minecraftVersion;
    public final String buildId;
    public final String mavenHost;

    public YarnConfiguredMappings(String mavenHost, String mappingType, String minecraftVersion, String buildId) {
        super(mappingType, minecraftVersion + "+build." + buildId);

        this.mavenHost = mavenHost;
        this.minecraftVersion = minecraftVersion;
        this.buildId = buildId;
    }

    @Override
    public void download() throws IOException {
        File mappingsDirectory = getMappingsDirectory();

        String merged = "https://" + this.mavenHost + "/net/fabricmc/yarn/" + this.mappingVersion + "/yarn-" + this.mappingVersion + "-mergedv2.jar";
        String regular = "https://" + this.mavenHost + "/net/fabricmc/yarn/" + this.mappingVersion + "/yarn-" + this.mappingVersion + ".jar";

        File mergedFile = new File(mappingsDirectory, "merged.jar");
        File regularFile = new File(mappingsDirectory, "regular.jar");

        IOUtility.download(new URL(merged), mergedFile, false);
        IOUtility.download(new URL(regular), regularFile, false);

        IOUtility.extract(mergedFile, s -> "mappings-merged.tiny","mappings/mappings.tiny");
        IOUtility.extract(regularFile, s -> "mappings.tiny","mappings/mappings.tiny");
    }

    @Override
    public String[] getMappingFiles() {
        return new String[] {
                "/mappings.tiny",
                "/mappings-merged.tiny"
        };
    }
}

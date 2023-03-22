package com.pocolifo.jarremapper.devplugin.config;

import com.pocolifo.jarremapper.devplugin.IOUtility;

import java.io.File;
import java.io.IOException;

import static com.pocolifo.jarremapper.devplugin.IOUtility.asUrl;

public class McpConfiguredMappings extends AbstractConfiguredMapping {
    public final String mcpChannel;
    public final String mcpVersion;
    public final String minecraftVersion;

    public McpConfiguredMappings(String mcpChannel, String mcpVersion, String minecraftVersion) {
        super("mcp", mcpChannel + "-" + mcpVersion);
        this.mcpChannel = mcpChannel;
        this.mcpVersion = mcpVersion;
        this.minecraftVersion = minecraftVersion;
    }

    @Override
    public void download() throws IOException {
        String csvUrl = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_" + mcpChannel + "/" + mcpVersion + "-" +
                minecraftVersion + "/mcp_" + mcpChannel + "-" + mcpVersion + "-" + minecraftVersion + ".zip";
        String srgUrl = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp/" + minecraftVersion + "/mcp-" + minecraftVersion + "-srg.zip";

        File mappingsDirectory = getMappingsDirectory();
        File csvFile = new File(mappingsDirectory, "csv.zip");
        File srgFile = new File(mappingsDirectory, "srg.zip");

        IOUtility.download(asUrl(csvUrl), csvFile, false);
        IOUtility.download(asUrl(srgUrl), srgFile, false);

        IOUtility.extract(csvFile, s -> s, "fields.csv", "methods.csv", "params.csv");
        IOUtility.extract(srgFile, s -> s, "joined.srg", "joined.exc");
    }

    @Override
    public String[] getMappingFiles() {
        return new String[] { "/" };
    }
}

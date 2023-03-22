package com.pocolifo.jarremapper.devplugin.config;

import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.io.IOException;

import static com.pocolifo.jarremapper.devplugin.JarRemapperDevPlugin.project;

public abstract class AbstractConfiguredMapping {
    public final String mappingType;
    public final String mappingVersion;

    public AbstractConfiguredMapping(String mappingType, String mappingVersion) {
        this.mappingType = mappingType;
        this.mappingVersion = mappingVersion;
    }

    protected File getMappingsDirectory() {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        String testResourcesPath = sourceSets.getByName("test").getResources().getSourceDirectories().getAsPath();

        File file = new File(testResourcesPath, "mappings/" + this.mappingType + "/" + this.mappingVersion);
        file.mkdirs();
        return file;
    }

    public abstract void download() throws IOException;
    public abstract String[] getMappingFiles();
}

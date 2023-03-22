package com.pocolifo.jarremapper.devplugin.config;

import com.pocolifo.jarremapper.devplugin.IOUtility;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.pocolifo.jarremapper.devplugin.IOUtility.asUrl;
import static com.pocolifo.jarremapper.devplugin.JarRemapperDevPlugin.project;

public class ConfiguredJar {
    public final String jarNamespace;
    public final String downloadUrl;
    public final List<AbstractConfiguredMapping> mappings;
    private final String jarFileName;

    public ConfiguredJar(String jarFileName, String downloadUrl, String jarNamespace, List<AbstractConfiguredMapping> mappings) {
        this.jarFileName = jarFileName;
        this.downloadUrl = downloadUrl;
        this.jarNamespace = jarNamespace;
        this.mappings = mappings;
    }

    public static File getJarDirectory() {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        String testResourcesPath = sourceSets.getByName("test").getResources().getSourceDirectories().getAsPath();

        File file = new File(testResourcesPath, "jars");
        file.mkdirs();
        return file;
    }

    public File getJarFile() {
        return new File(getJarDirectory(), this.jarFileName);
    }

    public void downloadJar() throws IOException {
        IOUtility.download(asUrl(this.downloadUrl), this.getJarFile(), false);
    }
}

package com.pocolifo.jarremapper.devplugin.task;

import com.pocolifo.jarremapper.devplugin.JarRemapperDevPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public class DownloadJarsAndMappingsTask extends DefaultTask {
    @TaskAction
    public void downloadResources() {
        JarRemapperDevPlugin.CONFIGURED_JARS.forEach(configuredJar -> {
            try {
                configuredJar.downloadJar();
            } catch (IOException e) {
                this.getLogger().error("Could not download JAR", e);
            }

            configuredJar.mappings.forEach(mapping -> {
                try {
                    mapping.download();
                } catch (IOException e) {
                    this.getLogger().error("Could not download mappings", e);
                }
            });
        });
    }
}

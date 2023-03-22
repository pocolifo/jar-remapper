package com.pocolifo.jarremapper.devplugin;

import com.pocolifo.jarremapper.devplugin.config.ConfiguredJar;
import com.pocolifo.jarremapper.devplugin.config.McpConfiguredMappings;
import com.pocolifo.jarremapper.devplugin.config.YarnConfiguredMappings;
import com.pocolifo.jarremapper.devplugin.task.DownloadJarsAndMappingsTask;
import com.pocolifo.jarremapper.devplugin.task.GenerateTestsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JarRemapperDevPlugin implements Plugin<Project> {
	// TODO: make configuration somewhere else maybe?
	public static final List<ConfiguredJar> CONFIGURED_JARS = Arrays.asList(
			new ConfiguredJar(
					"minecraft-1.8.9.jar",
					"https://piston-data.mojang.com/v1/objects/3870888a6c3d349d3771a3e9d16c9bf5e076b908/client.jar",
					"minecraft-1.8.9",
					Arrays.asList(
							new YarnConfiguredMappings(
									"maven.legacyfabric.net",
									"legacyfabric",
									"1.8.9",
									"202206020145"
							),
							new McpConfiguredMappings(
									"stable",
									"22",
									"1.8.9"
							)
					)
			),
			new ConfiguredJar(
					"minecraft-1.19.4.jar",
					"https://piston-data.mojang.com/v1/objects/958928a560c9167687bea0cefeb7375da1e552a8/client.jar",
					"minecraft-1.19.4",
					Collections.singletonList(
							new YarnConfiguredMappings(
									"maven.fabricmc.net",
									"fabric",
									"1.19.4",
									"1"
							)
					)
			)
	);

	public static Project project;

	@Override
	public void apply(Project p) {
		project = p;

		project.getTasks().register("downloadJarsAndMappings", DownloadJarsAndMappingsTask.class);
		project.getTasks().register("generateTests", GenerateTestsTask.class);
		project.getTasks().getByName("generateTests").dependsOn("downloadJarsAndMappings");

		project.getTasks().getByName("clean").doLast(task -> {
			SourceSetContainer sourceSets = task.getProject().getExtensions().getByType(SourceSetContainer.class);
			SourceSet testSet = sourceSets.getByName("test");
			File resources = new File(testSet.getResources().getSourceDirectories().getAsPath());

			File autogen = GenerateTestsTask.getAutogenDirectory(task.getProject());
			File jars = new File(resources, "jars");
			File mappings = new File(resources, "mappings");

			try {
				if (autogen.exists()) IOUtility.deleteDirectory(autogen);
				if (jars.exists()) IOUtility.deleteDirectory(jars);
				if (mappings.exists()) IOUtility.deleteDirectory(mappings);
			} catch (IOException e) {
				project.getLogger().error("Failed to delete autogen, jars, mapping directories", e);
			}
		});
	}
}

package com.pocolifo.jarremapper.devplugin.task;

import com.pocolifo.jarremapper.devplugin.IOUtility;
import com.pocolifo.jarremapper.devplugin.JarRemapperDevPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GenerateTestsTask extends DefaultTask {
    @TaskAction
    public void generateTests() throws IOException {
        JarRemapperDevPlugin.project = this.getProject();

        SourceSetContainer sourceSets = this.getProject().getExtensions().getByType(SourceSetContainer.class);
        SourceSet testSet = sourceSets.getByName("test");

        // get remapping methods
        List<String> remappers = new ArrayList<>();
        File remappersFile = testSet.getJava().getAsFileTree().filter(file -> file.getName().equals("Remappers.java")).getSingleFile();
        String remapperFileImport = remappersFile.getAbsolutePath()
                .replace(testSet.getAllJava().getSourceDirectories().getAsPath(), "")
                .replace(".java", "")
                .replace("/", ".")
                .substring(1);

        Files.readAllLines(remappersFile.toPath()).forEach(line -> {
            line = line.trim();

            boolean correctStartLine = line.startsWith("public static void ");
            boolean correctEndLine = line.endsWith("(File inputFile, JarMapping mapping) throws IOException {") || line.endsWith("(File inputFile, JarMapping mapping) {");

            if (correctStartLine && correctEndLine) {
                String methodName = line
                        .replace("public static void ", "")
                        .replace("(File inputFile, JarMapping mapping) throws IOException {", "")
                        .replace("(File inputFile, JarMapping mapping) {", "");

                remappers.add(methodName);
            }
        });

        // generate
        File autogenDirectory = getAutogenDirectory(this.getProject());
        autogenDirectory.mkdirs();

        IOUtility.copyResource("TestUtility.java", new File(autogenDirectory, "TestUtility.java"));

        JarRemapperDevPlugin.CONFIGURED_JARS.forEach(configuredJar -> {
            try {
                String testClassName = "TestJar_" + configuredJar.jarNamespace.replaceAll("-", "").replaceAll("\\.", "");
                String baseTest = new String(IOUtility.readResource("BaseTest.java"));
                StringBuilder imports = new StringBuilder();
                StringBuilder code = new StringBuilder();

                configuredJar.mappings.forEach(mapping -> {
                    for (String mappingFile : mapping.getMappingFiles()) {
                        remappers.forEach(remapper -> {
                            String mappingVersion = mapping.mappingVersion.replaceAll("\\.", "").replaceAll("\\+", "").replaceAll("-", "_");
                            String methodName = "test_" + remapper + "_" + mapping.mappingType + "_" + mappingVersion + "_" + mappingFile.replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", "");

                            code
                                    .append("@Test\n")
                                    .append("void ").append(methodName).append("() throws Exception {\n")
                                    .append("Remappers.").append(remapper).append("(TestUtility.getResourceAsFile(\"/jars/").append(configuredJar.getJarFile().getName()).append("\"), TestUtility.readMapping(\"/mappings/").append(mapping.mappingType).append("/").append(mapping.mappingVersion).append(mappingFile).append("\"));\n")
                                    .append("}\n");
                        });
                    }
                });


                imports.append("import ").append(remapperFileImport).append(";\n");

                baseTest = String.format(baseTest, imports, testClassName, code);

                // Write to file
                File testJavaFile = new File(autogenDirectory, testClassName + ".java");
                Files.write(testJavaFile.toPath(), baseTest.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                this.getLogger().error("Could not write test", e);
            }
        });
    }

    public static File getAutogenDirectory(Project project) {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet testSet = sourceSets.getByName("test");
        return new File(testSet.getAllJava().getSourceDirectories().getAsPath(), "autogen");
    }
}

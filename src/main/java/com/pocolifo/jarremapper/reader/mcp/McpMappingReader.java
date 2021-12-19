package com.pocolifo.jarremapper.reader.mcp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;
import com.pocolifo.jarremapper.reader.MappingUtility;
import org.objectweb.asm.Type;

public class McpMappingReader {
    private final File srgFile;
    private final File excFile;

    private final File methodsCsv;
    private final File fieldsCsv;
    private final File paramsCsv;

    public final Map<String, String> classes = new HashMap<>();
    public final Map<String, String> methods = new HashMap<>();
    public final Map<String, String> fields = new HashMap<>();
    public final Map<String, String> params = new HashMap<>();

    private final Map<String, String> srgMethods = new HashMap<>();

    public McpMappingReader(File srgFile, File excFile, File methodsCsv, File fieldsCsv, File paramsCsv) {
        this.srgFile = srgFile;
        this.excFile = excFile;

        this.methodsCsv = methodsCsv;
        this.fieldsCsv = fieldsCsv;
        this.paramsCsv = paramsCsv;
    }

    public JarMapping read() throws IOException {
        this.readSrg();

        this.srgMethods.putAll(this.methods);

        this.readCsv(this.methodsCsv, this.methods);
        this.readCsv(this.fieldsCsv, this.fields);

        // Read the parameters CSV
        for (String line : Files.readAllLines(this.paramsCsv.toPath())) {
            if (line.contains("searge")) continue;
            if (!line.contains(",")) continue;

            String[] split = line.split(",");

            this.params.put(split[0], split[1]);
        }

        JarMapping mapping = this.convertToJarMapping();

        this.readExc(mapping); // for constructor params?

        return mapping;
    }

    private void renameParameters(MethodMapping method) {
        for (int i = 0; method.parameterNames.size() > i; i++) {
            String parameterName = method.parameterNames.get(i);
            String newName = this.params.get(parameterName);

            method.parameterNames.set(i, newName == null ? parameterName : newName);
        }
    }

    private void readCsv(File csvFile, Map<String, String> map) throws IOException {
        Map<String, String> srgNamedMap = new HashMap<>();
        Map<String, String> emptyMap = new HashMap<>();

        for (String line : Files.readAllLines(csvFile.toPath())) {
            if (line.contains("searge")) continue;
            if (!line.contains(",")) continue;

            String[] split = line.split(",");

            srgNamedMap.put(split[0], split[1]);
        }

        for (Map.Entry<String, String> fromNameSrgName : map.entrySet()) {
            String srg = MappingUtility.getNameAfterLastSlash(MappingUtility.getMethodName(fromNameSrgName.getValue()));

            String named = srgNamedMap.get(
                    MappingUtility.getNameAfterLastSlash(MappingUtility.getMethodName(fromNameSrgName.getValue()))
            );

            if (named == null) named = srg;

            emptyMap.put(fromNameSrgName.getKey(), fromNameSrgName.getValue().replace(
                    srg,
                    named
            ));
        }

        map.clear();
        map.putAll(emptyMap);
    }

    private void readExc(JarMapping mapping) throws IOException {
        for (String line : Files.readAllLines(this.excFile.toPath())) {
            String[] split = line.split("=");

            int beginMethodName = split[0].lastIndexOf('.');
            int endMethodName = split[0].lastIndexOf('(');

            if (beginMethodName > -1 && endMethodName > -1) {
                String className = split[0].substring(0, beginMethodName);
                String methodName = split[0].substring(beginMethodName + 1, endMethodName);
                String methodDescriptor = split[0].substring(endMethodName);

                ClassMapping cls = mapping.getClassByToName(className);
                MethodMapping method = cls.getMethodByToName(methodName, methodDescriptor);

                if (method == null) {
                    method = new MethodMapping(methodName, methodDescriptor, methodName, methodDescriptor, cls);
                }

                if (split[1].charAt(0) == '|') {
                    // param
                    method.parameterNames.addAll(Arrays.asList(split[1].replaceAll("\\|", "").split(",")));
                } else {
                    // exception
                    method.exceptions.addAll(Arrays.asList(split[1].replaceAll("\\|", "").split(",")));
                }

                this.renameParameters(method);

                cls.methodMappings.add(method);
                mapping.classMappings.add(cls);
            }
        }
    }

    private void readSrg() throws IOException {
        List<String[]> classEntries = new ArrayList<>();
        List<String[]> fieldEntries = new ArrayList<>();
        List<String[]> methodEntries = new ArrayList<>();

        // get everything
        for (String line : Files.readAllLines(this.srgFile.toPath())) {
            String[] fields = line.split(" ");

            switch (fields[0]) {
                case "CL:":
                    classEntries.add(fields);
                    break;

                case "FD:":
                    fieldEntries.add(fields);
                    break;

                case "MD:":
                    methodEntries.add(fields);
                    break;

                // PK mappings don't make a difference
            }
        }

        for (String[] fields : classEntries) {
            this.classes.put(fields[1], fields[2]);
        }

        for (String[] fields : fieldEntries) {
            this.fields.put(fields[1], fields[2]);
        }

        for (String[] fields : methodEntries) {
            this.methods.put(fields[1] + " " + fields[2], fields[3] + " " + fields[4]);
        }
    }

    private List<String> inferParameterNames(String srgName, String methodDescriptor) {
        if (srgName.matches("func_\\d+_.+")) {
            List<String> params = new ArrayList<>();

            // TODO: Last parameter does not get named sometimes, FIX
            // tried Type.getArgumentsAndReturnSizes(methodDescriptor), works better, but not the best

            Type[] arguments = Type.getArgumentTypes(methodDescriptor);
            for (int i = 0; arguments.length > i; i++) {
                params.add("p_" + srgName.substring(5,
                        srgName.indexOf('_', 5)) + "_" + i + "_");
            }

            return params;
        }

        return Collections.emptyList();
    }

    public JarMapping convertToJarMapping() {
        JarMapping mapping = new JarMapping();

        for (Map.Entry<String, String> cls : this.classes.entrySet()) {
            mapping.classMappings.add(new ClassMapping(cls.getKey(), cls.getValue(), mapping));
        }

        for (Map.Entry<String, String> md : this.methods.entrySet()) {
            String from_cls = MappingUtility.getFromClassName(MappingUtility.getMethodName(md.getKey()));
            String from_name = MappingUtility.getNameAfterLastSlash(MappingUtility.getMethodName(md.getKey()));
            String from_desc = MappingUtility.getMethodDescriptor(md.getKey());

            String to_cls = MappingUtility.getFromClassName(MappingUtility.getMethodName(md.getValue()));
            String to_name = MappingUtility.getNameAfterLastSlash(MappingUtility.getMethodName(md.getValue()));
            String to_desc = MappingUtility.getMethodDescriptor(md.getValue());

            ClassMapping cls = mapping.getClassByFromName(from_cls);

            MethodMapping method = new MethodMapping(from_name, from_desc, to_name, to_desc, cls);

            method.parameterNames.addAll(this.inferParameterNames(
                    MappingUtility.getNameAfterLastSlash(MappingUtility.getMethodName(this.srgMethods.get(md.getKey()))),
                    from_desc));

            this.renameParameters(method);

            cls.methodMappings.add(method);
        }

        for (Map.Entry<String, String> fd : this.fields.entrySet()) {
            String from_cls = MappingUtility.getFromClassName(fd.getKey());
            String from_name = MappingUtility.getNameAfterLastSlash(fd.getKey());

            String to_cls = MappingUtility.getFromClassName(fd.getValue());
            String to_name = MappingUtility.getNameAfterLastSlash(fd.getValue());

            ClassMapping cls = mapping.getClassByFromName(from_cls);
            cls.fieldMappings.add(new FieldMapping(from_name, to_name, cls));
        }

        return mapping;
    }
}

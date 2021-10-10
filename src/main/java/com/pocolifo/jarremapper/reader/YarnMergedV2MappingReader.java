package com.pocolifo.jarremapper.reader;

import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class YarnMergedV2MappingReader {
    private final File mappingFile;

    public YarnMergedV2MappingReader(File mappingFile) {
        this.mappingFile = mappingFile;
    }

    public JarMapping read() throws IOException {
        JarMapping mapping = new JarMapping();
        ClassMapping currentClass = null;
        MethodMapping currentMethod = null;

        for (String line : Files.readAllLines(this.mappingFile.toPath())) {
            String[] fields = line.split("\t");

            // it's a class
            if (fields[0].equals("c")) {
                if (currentMethod != null) {
                    currentClass.methodMappings.add(currentMethod);
                }

                if (currentClass != null) {
                    mapping.classMappings.add(currentClass);
                    currentClass = null;
                }

                currentClass = new ClassMapping(fields[1], fields[3], mapping);
            } else if (fields[0].isEmpty() && fields[1].equals("f")) {
                if (currentMethod != null) {
                    currentClass.methodMappings.add(currentMethod);
                }

                currentClass.fieldMappings.add(new FieldMapping(fields[3], fields[5], currentClass));
            } else if (fields[0].isEmpty() && fields[1].equals("m")) {
                if (currentMethod != null) {
                    currentClass.methodMappings.add(currentMethod);
                    currentMethod = null;
                }

                currentMethod = new MethodMapping(fields[3], fields[2], fields[5], fields[2], currentClass);
            } else if (fields[0].isEmpty() && fields[1].isEmpty() && fields[2].equals("p")) {
                int index = Integer.parseInt(fields[3]);

                while (index + 1 > currentMethod.parameterNames.size()) {
                    currentMethod.parameterNames.add("var" + index);
                }

                currentMethod.parameterNames.set(index, fields[fields.length - 1]);
            }
        }

        if (currentMethod != null) {
            currentClass.methodMappings.add(currentMethod);
        }

        mapping.classMappings.add(currentClass);

        return mapping;
    }
}

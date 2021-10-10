package com.pocolifo.jarremapper.reader;

import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class YarnV1MappingReader {

    public final Map<String, String> classes = new HashMap<>();
    public final Map<String, String> fields = new HashMap<>();
    public final Map<String, String> methods = new HashMap<>();

    private final File mappingFile;

    public YarnV1MappingReader(File mappingFile) {
        this.mappingFile = mappingFile;
    }

    public JarMapping read() throws IOException {
        this.loadIntoMaps();
        return this.convertToJarMapping();
    }

    private JarMapping convertToJarMapping() throws IOException {
        JarMapping mapping = new JarMapping();

        // logic
        for (Map.Entry<String, String> cls : this.classes.entrySet()) {
            mapping.classMappings.add(new ClassMapping(cls.getKey(), cls.getValue(), mapping));
        }

        for (Map.Entry<String, String> md : this.methods.entrySet()) {
            String from_cls = MappingUtility.getFromClassName(MappingUtility.getMethodName(md.getKey()));
            String from_name = MappingUtility.getNameAfterLastSlash(MappingUtility.getMethodName(md.getKey()));
            String from_desc = MappingUtility.getMethodDescriptor(md.getKey());

            String to_name = md.getValue();

            ClassMapping cls = mapping.getClassByFromName(from_cls);
            cls.methodMappings.add(new MethodMapping(from_name, from_desc, to_name, from_desc, cls));
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

    private void loadIntoMaps() throws IOException {
        for (String line : Files.readAllLines(this.mappingFile.toPath())) {
            String[] fields = line.split("\t");

            switch (fields[0]) {
                // v1	official	intermediary	named
                // CLASS	tv$1	net/minecraft/class_1865$1	net/minecraft/entity/passive/SheepEntity$1
                case "CLASS":
                    this.classes.put(fields[1], fields[3]);
                    break;

                // v1	official	intermediary	named
                // FIELD	um	Ldc;	bn	field_8033	rightArmAngle
                case "FIELD":
                    this.fields.put(fields[1] + "/" + fields[3], this.classes.get(fields[1]) + "/" + fields[5]);
                    break;

                // v1	official	intermediary	named
                //         class descriptor official intermediary      named
                // METHOD	pm	(ILadm;)Lpk;	a	method_7067	createInstanceFromId
                case "METHOD":
                    this.methods.put(fields[1] + "/" + fields[3] + " " + fields[2], fields[5]);
                    break;
            }
        }
    }
}

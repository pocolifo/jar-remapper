package com.pocolifo.jarremapper.mapping;

import java.util.ArrayList;
import java.util.List;

public class ClassMapping {
    public final String fromClassName;
    public final String toClassName;
    public final JarMapping parentJar;


    // field mappings inside this class
    public final List<FieldMapping> fieldMappings = new ArrayList<>();

    // method mappings inside this class
    public final List<MethodMapping> methodMappings = new ArrayList<>();

    public ClassMapping(String fromClassName, String toClassName, JarMapping parentJar) {
        this.fromClassName = fromClassName;
        this.toClassName = toClassName;
        this.parentJar = parentJar;
    }

    public MethodMapping getMethodByFromName(String fromName, String fromDesc) {
        for (MethodMapping methodMapping : this.methodMappings) {
            if (methodMapping.fromMethodName.equals(fromName)) {
                if (fromDesc == null) {
                    return methodMapping;
                } else if (methodMapping.fromMethodDescriptor.equals(fromDesc)) {
                    return methodMapping;
                }
            }
        }

        return null;
    }

    public MethodMapping getMethodByToName(String toName, String toDesc) {
        for (MethodMapping methodMapping : this.methodMappings) {
            if (methodMapping.toMethodName.equals(toName)) {
                if (toDesc == null) {
                    return methodMapping;
                } else if (methodMapping.toMethodDescriptor.equals(toDesc)) {
                    return methodMapping;
                }
            }
        }

        return null;
    }

    public FieldMapping getFieldByFromName(String fromName) {
        for (FieldMapping fieldMapping : this.fieldMappings) {
            if (fieldMapping.fromFieldName.equals(fromName)) return fieldMapping;
        }

        return null;
    }

    public FieldMapping getFieldByToName(String toName) {
        for (FieldMapping fieldMapping : this.fieldMappings) {
            if (fieldMapping.toFieldName.equals(toName)) return fieldMapping;
        }

        return null;
    }

    @Override
    public String toString() {
        return this.fromClassName + " | " + this.toClassName;
    }
}

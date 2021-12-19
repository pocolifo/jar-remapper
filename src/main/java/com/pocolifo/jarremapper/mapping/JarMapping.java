package com.pocolifo.jarremapper.mapping;

import java.util.ArrayList;
import java.util.List;

public class JarMapping {
    public final List<ClassMapping> classMappings = new ArrayList<>();

    public ClassMapping getClassByFromName(String fromName) {
        for (ClassMapping classMapping : this.classMappings) {
            if (classMapping.fromClassName.equals(fromName)) return classMapping;
        }

        return null;
    }

    public ClassMapping getClassByToName(String toName) {
        for (ClassMapping classMapping : this.classMappings) {
            if (classMapping.toClassName.equals(toName)) return classMapping;
        }

        return null;
    }

    public void reverse() {
        List<ClassMapping> reversed = new ArrayList<>();

        for (ClassMapping oldClassMapping : this.classMappings) {
            ClassMapping newClassMapping = new ClassMapping(oldClassMapping.toClassName, oldClassMapping.fromClassName,
                    this);

            for (MethodMapping oldMethodMapping : oldClassMapping.methodMappings) {
                MethodMapping newMethodMapping = new MethodMapping(oldMethodMapping.toMethodName,
                        oldMethodMapping.toMethodDescriptor, oldMethodMapping.fromMethodName,
                        oldMethodMapping.fromMethodDescriptor, newClassMapping);

                for (int i = 0; i < oldMethodMapping.parameterNames.size(); i++) {
                    newMethodMapping.parameterNames.add("var" + i);
                }

                newClassMapping.methodMappings.add(newMethodMapping);
            }

            for (FieldMapping oldFieldMapping : oldClassMapping.fieldMappings) {
                FieldMapping newFieldMapping = new FieldMapping(oldFieldMapping.toFieldName,
                        oldFieldMapping.fromFieldName, newClassMapping);

                newClassMapping.fieldMappings.add(newFieldMapping);
            }


            reversed.add(newClassMapping);
        }

        this.classMappings.clear();
        this.classMappings.addAll(reversed);
        reversed.clear();
    }
}

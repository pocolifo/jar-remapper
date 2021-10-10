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
}

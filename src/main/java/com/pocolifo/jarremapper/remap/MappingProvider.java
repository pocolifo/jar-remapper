package com.pocolifo.jarremapper.remap;

import com.pocolifo.jarremapper.JarRemapper;
import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

public class MappingProvider extends Remapper {
    private final JarMapping jarMapping;
    private final JarRemapper jarRemapper;

    private final ClassNode currentClass;

    public MappingProvider(JarMapping mapping, ClassNode currentClass, JarRemapper remapper) {
        this.jarMapping = mapping;
        this.jarRemapper = remapper;

        this.currentClass = currentClass;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        String methodName = this.getMethodName(owner, name, descriptor);
        return methodName == null ? super.mapMethodName(owner, name, descriptor) : methodName;
    }

    private String getMethodName(String owner_fromName, String name_fromName, String descriptor) {
        ClassMapping cm = this.jarMapping.getClassByFromName(owner_fromName);

        if (cm == null) return name_fromName; // most likely from a Minecraft dependency

        MethodMapping mm = cm.getMethodByFromName(name_fromName, descriptor);

        if (mm == null) { // most likely in a parent class
            for (String parentClass : this.getParents(this.jarRemapper.getClassNode(owner_fromName))) {
                String methodName = this.getMethodName(parentClass, name_fromName, descriptor);
                if (!methodName.equals(name_fromName)) return methodName;
            }
        }

        return mm == null ? name_fromName : mm.toMethodName;
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        String fieldName = this.getFieldName(owner, name, descriptor);
        return fieldName == null ? super.mapFieldName(owner, name, descriptor) : fieldName;
    }

    private String getFieldName(String owner_fromName, String name_fromName, String descriptor) {
        ClassMapping cm = this.jarMapping.getClassByFromName(owner_fromName);
        if (cm == null) return name_fromName; // most likely from a Minecraft dependency

        FieldMapping fm = cm.getFieldByFromName(name_fromName);

        if (fm == null) { // most likely in a parent class
            for (String parentClass : this.getParents(this.jarRemapper.getClassNode(owner_fromName))) {
                String fieldName = this.getFieldName(parentClass, name_fromName, descriptor);
                if (!fieldName.equals(name_fromName)) return fieldName;
            }
        }

        return fm == null ? name_fromName : fm.toFieldName;
    }

    @Override
    public String map(String internalName) {
        ClassMapping cls = this.jarMapping.getClassByFromName(internalName);

        if (cls != null) return cls.toClassName;

        return super.map(internalName);
    }

    private List<String> getParents(ClassNode currentClass) {
        List<String> parents = new ArrayList<>();

        if (currentClass.superName != null) {
            parents.add(currentClass.superName);
        }

        parents.addAll(currentClass.interfaces);


        return parents;
    }
}

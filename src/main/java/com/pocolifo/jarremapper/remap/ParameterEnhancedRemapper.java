package com.pocolifo.jarremapper.remap;

import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

public class ParameterEnhancedRemapper extends ClassRemapper {
    private final JarMapping mapping;
    private final MappingProvider mappingProvider;

    private String currentClass;

    public ParameterEnhancedRemapper(MappingProvider remapper, ClassNode node, JarMapping mapping) {
        super(node, remapper);

        this.mapping = mapping;
        this.mappingProvider = remapper;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentClass = name;

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        ClassMapping classMapping = this.mapping.getClassByFromName(this.currentClass);
        ClassNode cn = (ClassNode) this.cv;

        for (MethodNode method : cn.methods) {
            if (method.localVariables == null) continue;

            MethodMapping methodMapping = null;
            if (classMapping != null) methodMapping = classMapping.getMethodByToName(method.name, method.desc);

            for (int i = 0; method.localVariables.size() > i; i++) {
                LocalVariableNode lvn = method.localVariables.get(i);

                // TODO: remove this, seems to be working fine
//                if (lvn.name.charAt(0) != 0x2603) {
//                    continue;
//                }

                // TODO: often misses first parameter name, fix
                if (methodMapping != null && methodMapping.parameterNames.size() > i) {
                    lvn.name = methodMapping.parameterNames.get(i);
                } else {
                    lvn.name = "var" + i;
                }
            }
        }

        super.visitEnd();
    }
}

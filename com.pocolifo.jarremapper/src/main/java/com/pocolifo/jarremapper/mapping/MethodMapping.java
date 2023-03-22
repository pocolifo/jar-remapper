package com.pocolifo.jarremapper.mapping;

import java.util.ArrayList;
import java.util.List;

public class MethodMapping {
    public final String fromMethodName;
    public final String fromMethodDescriptor;
    public final String toMethodName;
    public final String toMethodDescriptor;
    public final ClassMapping parentClass;

    public MethodMapping(String fromMethodName, String fromMethodDescriptor, String toMethodName,
                         String toMethodDescriptor, ClassMapping parentClass) {
        this.fromMethodName = fromMethodName;
        this.fromMethodDescriptor = fromMethodDescriptor;
        this.toMethodName = toMethodName;
        this.toMethodDescriptor = toMethodDescriptor;
        this.parentClass = parentClass;
    }

    public List<String> exceptions = new ArrayList<>();
    public List<String> parameterNames = new ArrayList<>();

    @Override
    public String toString() {
        return this.fromMethodName + " " + this.fromMethodDescriptor + " | " + this.toMethodName + " " +
                this.toMethodDescriptor + " (" + this.parentClass + ")";
    }
}

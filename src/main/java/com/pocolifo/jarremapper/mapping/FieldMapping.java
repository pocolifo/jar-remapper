package com.pocolifo.jarremapper.mapping;

public class FieldMapping {
    public final String fromFieldName;
    public final String toFieldName;
    public final ClassMapping parentClass;

    public FieldMapping(String fromFieldName, String toFieldName, ClassMapping parentClass) {
        this.fromFieldName = fromFieldName;
        this.toFieldName = toFieldName;
        this.parentClass = parentClass;
    }

    @Override
    public String toString() {
        return this.fromFieldName + " | " + this.toFieldName + " (" + this.parentClass
                + ")";
    }
}

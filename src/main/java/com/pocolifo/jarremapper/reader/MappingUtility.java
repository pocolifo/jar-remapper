package com.pocolifo.jarremapper.reader;

public class MappingUtility {
    public static String getNameAfterLastSlash(String str) {
        return str.substring(str.lastIndexOf("/") + 1);
    }

    public static String getFromClassName(String field) {
        return field.substring(0, field.lastIndexOf("/"));
    }

    public static String getMethodName(String str) {
        return str.split(" ")[0];
    }

    public static String getMethodDescriptor(String str) {
        return str.split(" ")[1];
    }
}

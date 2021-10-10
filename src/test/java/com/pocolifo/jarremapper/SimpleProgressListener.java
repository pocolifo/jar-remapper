package com.pocolifo.jarremapper;

import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.plugin.IRemappingPlugin;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SimpleProgressListener implements IRemappingPlugin {

    private long start;
    private long startClass;

    private int classCount;
    private int remappedCount;

    @Override
    public void onBeginRemap(ZipFile remappingJar) {
        this.start = System.currentTimeMillis();

        Enumeration<? extends ZipEntry> entries = remappingJar.entries();

        while (entries.hasMoreElements()) {
            if (entries.nextElement().getName().endsWith(".class")) this.classCount++;
        }

        System.out.println("Beginning remap");
    }

    @Override
    public void onBeforeRemapClass(ClassMapping classMapping) {
        this.startClass = System.currentTimeMillis();
    }

    @Override
    public void onAfterRemapClass(ClassMapping classRemapped) {
        this.remappedCount++;

        // exact progress
//        System.out.println(
//                ((System.currentTimeMillis() - this.startClass) / 1000f) + "s | " +
//                (classRemapped == null ? "(class mapping not found) " : classRemapped.toClassName) + " | " +
//                ((float) this.remappedCount / (float) this.classCount) * 100f + "% complete");
    }

    @Override
    public void onDoneRemap() {
        System.out.println("Done remapping, took " + ((System.currentTimeMillis() - start) / 1000f) + "s");
    }

}

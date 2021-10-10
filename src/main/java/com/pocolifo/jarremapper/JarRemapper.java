package com.pocolifo.jarremapper;

import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.plugin.IRemappingPlugin;
import com.pocolifo.jarremapper.remap.MappingProvider;
import com.pocolifo.jarremapper.remap.ParameterEnhancedRemapper;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class JarRemapper {
    private final Builder builder;
    private ZipFile zipFile;

    public JarRemapper(Builder builder) {
        this.builder = builder;
    }

    public static JarRemapper.Builder newBuilder() {
        return new JarRemapper.Builder();
    }

    private void remap() {
        // ensure we can remap the jar
        assert this.builder.inputFile != null;
        assert this.builder.inputFile.exists();

        assert this.builder.outputFile != null;
        assert !this.builder.outputFile.exists();
        
        assert this.builder.jarMapping != null;

        // actually remap the jar

        // output stream for output jar
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(this.builder.outputFile))) {
            // zip file to read the input jar
            try (ZipFile file = new ZipFile(this.builder.inputFile)) {
                this.zipFile = file;

                // beginning to remap the jar
                if (this.builder.remappingPlugin != null) this.builder.remappingPlugin.onBeginRemap(this.zipFile);

                Enumeration<? extends ZipEntry> entries = file.entries();

                // loop through every file in the jar
                while (entries.hasMoreElements()) {
                    // get the file entry in the jar
                    ZipEntry entry = entries.nextElement();

                    if (this.builder.excludeMetaInf && entry.getName().startsWith("META-INF")) {
                        outputStream.closeEntry();
                        continue;
                    }

                    // information about the class to insert into the
                    // remapped jar
                    ZipEntry newEntry;
                    byte[] newBytes;

                    // get the input stream for the current zip entry
                    try (InputStream stream = file.getInputStream(entry)) {
                        if (entry.getName().endsWith(".class")) {
                            // it's a class

                            // --------------------- //
                            // GET CLASS INFORMATION //
                            // --------------------- //

                            // initialize a class node to store class information
                            ClassNode currentClass = new ClassNode();

                            // make a class reader and give it the class input stream
                            ClassReader classReader = new ClassReader(stream);

                            // read the class information and store it in the class node!
                            classReader.accept(currentClass, ClassReader.SKIP_CODE);

                            if (this.builder.remappingPlugin != null)
                                this.builder.remappingPlugin.onBeforeRemapClass(
                                        this.builder.jarMapping.getClassByFromName(currentClass.name));

                            // -------------------- //
                            // MANIPULATE THE CLASS //
                            // -------------------- //
                            ClassNode remappedClass = new ClassNode();

                            MappingProvider remapper = new MappingProvider(this.builder.jarMapping, remappedClass, this);

                            ClassRemapper classRemapper = new ParameterEnhancedRemapper(remapper, remappedClass, this.builder.jarMapping);
                            classReader.accept(classRemapper, ClassReader.EXPAND_FRAMES); // must be EXPAND_FRAMES to fully read the class

                            // ----------------------- //
                            // WRITE OUT THE NEW CLASS //
                            // ----------------------- //
                            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                            remappedClass.accept(classWriter);

                            // ------------------------ //
                            // ADD THE CLASS TO THE JAR //
                            // ------------------------ //
                            newEntry = new ZipEntry(remappedClass.name + ".class");
                            newBytes = classWriter.toByteArray();

                            // done remapping class
                            if (this.builder.remappingPlugin != null)
                                this.builder.remappingPlugin.onAfterRemapClass(
                                        this.builder.jarMapping.getClassByToName(remappedClass.name));
                        } else {
                            // copy the file into the new jar -- it's a resource

                           newEntry = entry;
                           newBytes = IOUtils.toByteArray(stream);
                        }

                        // write out the new/remapped zip entry
                        outputStream.putNextEntry(newEntry);
                        outputStream.write(newBytes);
                        outputStream.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // done remapping the jar
        if (this.builder.remappingPlugin != null) this.builder.remappingPlugin.onDoneRemap();
    }

    // TODO: this should not be visible publicly, change at some point
    public ClassNode getClassNode(String fromName) {
        try {
            // initialize a class node to store class information
            ClassNode currentClass = new ClassNode();

            // make a class reader and give it the class input stream
            ClassReader classReader = new ClassReader(this.zipFile.getInputStream(
                    this.zipFile.getEntry(fromName + ".class")));

            // read the class information and store it in the class node!
            classReader.accept(currentClass, ClassReader.SKIP_CODE);

            return currentClass;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Builder {
        private File inputFile;
        private File outputFile;
        private JarMapping jarMapping;
        private IRemappingPlugin remappingPlugin;
        private boolean excludeMetaInf;

        // options
        public Builder setInputFile(File inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public Builder setOutputFile(File outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder setJarMapping(JarMapping jarMapping) {
            this.jarMapping = jarMapping;
            return this;
        }

        public Builder setRemappingPlugin(IRemappingPlugin remappingPlugin) {
            this.remappingPlugin = remappingPlugin;
            return this;
        }

        public Builder excludeMetaInf() {
            this.excludeMetaInf = !this.excludeMetaInf;
            return this;
        }

        // getters
        public File getInputFile() {
            return this.inputFile;
        }

        public File getOutputFile() {
            return this.outputFile;
        }

        public JarMapping getJarMapping() {
            return this.jarMapping;
        }

        public IRemappingPlugin getRemappingPlugin() {
            return this.remappingPlugin;
        }

        public boolean isExcludingMetaInf() {
            return this.excludeMetaInf;
        }

        // build method
        public void remap() {
            new JarRemapper(this).remap();
        }
    }
}

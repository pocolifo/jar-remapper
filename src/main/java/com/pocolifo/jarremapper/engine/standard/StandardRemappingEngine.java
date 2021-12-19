package com.pocolifo.jarremapper.engine.standard;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.pocolifo.jarremapper.Utility;
import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

public class StandardRemappingEngine extends AbstractRemappingEngine {
	private IRemappingPlugin remappingPlugin;
	private boolean excludeMetaInf;
	private ZipFile zipFile;

	public StandardRemappingEngine setRemappingPlugin(IRemappingPlugin remappingPlugin) {
		this.remappingPlugin = remappingPlugin;
		return this;
	}

	public StandardRemappingEngine excludeMetaInf() {
		this.excludeMetaInf = !this.excludeMetaInf;
		return this;
	}

	// getters
	public IRemappingPlugin getRemappingPlugin() {
		return this.remappingPlugin;
	}

	public boolean isExcludingMetaInf() {
		return this.excludeMetaInf;
	}

	@Override
	public void remap() throws IOException {
		this.zipFile = new ZipFile(this.inputFile);

		// actually remap the jar

		// output stream for output jar

		try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(this.outputFile))) {
			// zip file to read the input jar
			try (ZipFile file = new ZipFile(this.inputFile)) {
				this.zipFile = file;

				// beginning to remap the jar
				if (this.remappingPlugin != null) this.remappingPlugin.onBeginRemap(this.zipFile);

				Enumeration<? extends ZipEntry> entries = file.entries();

				// loop through every file in the jar
				while (entries.hasMoreElements()) {
					// get the file entry in the jar
					ZipEntry entry = entries.nextElement();

					if (this.excludeMetaInf && entry.getName().startsWith("META-INF")) {
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

							if (this.remappingPlugin != null)
								this.remappingPlugin.onBeforeRemapClass(
										this.mapping.getClassByFromName(currentClass.name));

							// -------------------- //
							// MANIPULATE THE CLASS //
							// -------------------- //
							ClassNode remappedClass = new ClassNode();

							MappingProvider remapper = new MappingProvider(this.mapping, remappedClass, this);

							ClassRemapper classRemapper = new ParameterEnhancedRemapper(remapper, remappedClass, this.mapping);
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
							if (this.remappingPlugin != null)
								this.remappingPlugin.onAfterRemapClass(
										this.mapping.getClassByToName(remappedClass.name));
						} else {
							// copy the file into the new jar -- it's a resource

							newEntry = entry;
							newBytes = Utility.readInputStream(stream);
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
		if (this.remappingPlugin != null) this.remappingPlugin.onDoneRemap();
	}

	ClassNode getClassNode(String fromName) {
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
}


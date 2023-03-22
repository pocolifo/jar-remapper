package com.pocolifo.jarremapper.extensions;

import java.io.IOException;

import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;
import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.JarProvider;
import net.md_5.specialsource.provider.JointProvider;

public class SpecialSourceEngine extends AbstractRemappingEngine {
	private boolean excludeMetaInf;

	public SpecialSourceEngine excludeMetaInf() {
		this.excludeMetaInf = !this.excludeMetaInf;
		return this;
	}

	@Override
	public void remap() throws IOException {
		JarMapping jarMapping = this.convertJarMapping();
		Jar jar = Jar.init(this.inputFile);

		JointProvider inheritanceProviders = new JointProvider();
		inheritanceProviders.add(new JarProvider(jar));
		jarMapping.setFallbackInheritanceProvider(inheritanceProviders);

		JarRemapper remapper = new JarRemapper(jarMapping);
		remapper.remapJar(jar, this.outputFile);

		ExtensionUtility.copyResources(this.inputFile, this.outputFile);
		if (this.excludeMetaInf) ExtensionUtility.removeMetaInf(this.outputFile);
	}

	public JarMapping convertJarMapping() {
		JarMapping jarMapping = new JarMapping();

		for (ClassMapping classMapping : this.mapping.classMappings) {
			jarMapping.classes.put(classMapping.fromClassName, classMapping.toClassName);

			for (FieldMapping fieldMapping : classMapping.fieldMappings) {
				jarMapping.fields.put(String.format("%s/%s", classMapping.fromClassName,
								fieldMapping.fromFieldName), fieldMapping.toFieldName);
			}

			for (MethodMapping methodMapping : classMapping.methodMappings) {
				jarMapping.methods.put(
						String.format("%s/%s %s",
								classMapping.fromClassName,
								methodMapping.fromMethodName,
								methodMapping.fromMethodDescriptor),
						methodMapping.toMethodName);
			}

		}

		return jarMapping;
	}
}

package com.pocolifo.jarremapper.extensions;

import java.io.IOException;
import java.nio.file.Path;

import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;
import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;

public class TinyRemapperEngine extends AbstractRemappingEngine {
	private TinyRemapper.Builder options = TinyRemapper.newRemapper();
	private boolean excludeMetaInf;
	private Path[] classpath;

	public TinyRemapperEngine setOptions(TinyRemapper.Builder options) {
		this.options = options;
		return this;
	}

	public TinyRemapperEngine excludeMetaInf() {
		this.excludeMetaInf = !this.excludeMetaInf;
		return this;
	}

	public TinyRemapperEngine setClasspath(Path... classpath) {
		this.classpath = classpath;
		return this;
	}

	@Override
	public void remap() throws IOException {
		TinyRemapper remapper = this.options
				.withMappings(createProvider(this.mapping))
				.ignoreFieldDesc(true)
				.build();

		remapper.readInputs(this.inputFile.toPath());
		if (this.classpath != null) remapper.readClassPath(this.classpath);

		try (OutputConsumerPath path = new OutputConsumerPath.Builder(this.outputFile.toPath()).build()) {
			remapper.apply(path);
		} finally {
			remapper.finish();
		}

		// copy resources
		ExtensionUtility.copyResources(this.inputFile, this.outputFile);
		if (this.excludeMetaInf) ExtensionUtility.removeMetaInf(this.outputFile);
	}

	public static IMappingProvider createProvider(JarMapping mapping) {
		return acceptor -> {
			for (ClassMapping cls : mapping.classMappings) {
				acceptor.acceptClass(cls.fromClassName, cls.toClassName);

				for (FieldMapping fld : cls.fieldMappings) {
					acceptor.acceptField(new IMappingProvider.Member(cls.fromClassName, fld.fromFieldName, null), fld.toFieldName);
				}

				for (MethodMapping mtd : cls.methodMappings) {
					IMappingProvider.Member mtdMember = new IMappingProvider.Member(cls.fromClassName, mtd.fromMethodName, mtd.fromMethodDescriptor);

					acceptor.acceptMethod(mtdMember, mtd.toMethodName);

					// TODO: Method parameters
				}
			}
		};
	}
}

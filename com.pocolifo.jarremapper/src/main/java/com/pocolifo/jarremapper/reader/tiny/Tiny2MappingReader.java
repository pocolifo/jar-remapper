package com.pocolifo.jarremapper.reader.tiny;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;

public class Tiny2MappingReader {
	private final File mappingFile;
	private final List<String> namespaces;

	public Tiny2MappingReader(File mappingFile) {
		this.mappingFile = mappingFile;

		this.namespaces = new LinkedList<>();
	}

	public JarMapping read(String fromNamespace, String toNamespace) throws IOException {
		int lineNumber = 0;

		JarMapping mapping = new JarMapping();

		ClassMapping currentClass = null;
		MethodMapping currentMethod = null;

		int fromIndex = 0;
		int toIndex = 0;

		for (String line : Files.readAllLines(this.mappingFile.toPath())) {
			String[] fields = line.split("\t");

			if (lineNumber == 0) {
				assert fields[0].equals("tiny") && fields[1].equals("2");
				this.namespaces.addAll(Arrays.asList(fields).subList(3, fields.length));

				fromIndex = this.namespaces.indexOf(fromNamespace);
				toIndex = this.namespaces.indexOf(toNamespace);
			} else {
				if (fields[0].equals("c")) {
					if (currentMethod != null) {
						currentClass.methodMappings.add(currentMethod);
					}

					if (currentClass != null) {
						mapping.classMappings.add(currentClass);
						currentClass = null;
					}

					currentClass = new ClassMapping(fields[fromIndex + 1], fields[toIndex + 1], mapping);
				} else if (fields[0].isEmpty() && fields[1].equals("f")) {
					if (currentMethod != null) {
						currentClass.methodMappings.add(currentMethod);
					}

					currentClass.fieldMappings.add(new FieldMapping(fields[fromIndex + 3], fields[toIndex + 3], currentClass));
				} else if (fields[0].isEmpty() && fields[1].equals("m")) {
					if (currentMethod != null) {
						currentClass.methodMappings.add(currentMethod);
						currentMethod = null;
					}

					currentMethod = new MethodMapping(fields[fromIndex + 3], fields[2], fields[toIndex + 3], fields[2], currentClass);
				} else if (fields[0].isEmpty() && fields[1].isEmpty() && fields[2].equals("p")) {
					int index = Integer.parseInt(fields[3]);

					while (index + 1 > currentMethod.parameterNames.size()) {
						currentMethod.parameterNames.add("var" + index);
					}

					currentMethod.parameterNames.set(index, fields[fields.length - 1]);
				}
			}

			lineNumber++;
		}

		if (currentMethod != null) {
			currentClass.methodMappings.add(currentMethod);
		}

		mapping.classMappings.add(currentClass);

		return mapping;
	}
}

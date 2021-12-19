package com.pocolifo.jarremapper.reader.tiny;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;

public class Tiny1MappingReader {
	private final File file;
	private final List<String> namespaces;
	private final JarMapping mapping;

	public Tiny1MappingReader(File file) {
		this.file = file;
		this.namespaces = new LinkedList<>();
		this.mapping = new JarMapping();
	}

	public JarMapping read(String fromNamespace, String toNamespace) throws IOException {
		int lineNumber = 0;

		for (String line : Files.readAllLines(this.file.toPath())) {
			String[] fields = line.split("\t");

			if (lineNumber == 0) {
				assert fields[0].equals("v1");
				this.namespaces.addAll(Arrays.asList(fields).subList(1, fields.length));
			} else {
				String type = fields[0].toLowerCase();

				switch (type) {
					case "class": {
						int i = 0;
						Map<String, String> namespaceClassMap = new LinkedHashMap<>();

						for (String mapping : Arrays.asList(fields).subList(1, fields.length)) {
							namespaceClassMap.put(this.namespaces.get(i), mapping);
							i++;
						}

						this.mapping.classMappings.add(new ClassMapping(namespaceClassMap.get(fromNamespace), namespaceClassMap.get(toNamespace), this.mapping));
						break;
					}
					case "field": {
						int i = 0;
						Map<String, String> namespaceFieldMap = new LinkedHashMap<>();

						for (String mapping : Arrays.asList(fields).subList(3, fields.length)) {
							namespaceFieldMap.put(this.namespaces.get(i), mapping);
							i++;
						}

						ClassMapping parentClass = this.mapping.getClassByFromName(fields[1]);
						parentClass.fieldMappings.add(new FieldMapping(namespaceFieldMap.get(fromNamespace), namespaceFieldMap.get(toNamespace), parentClass));
						break;
					}
					case "method": {
						int i = 0;
						Map<String, String> namespaceMethodMap = new LinkedHashMap<>();

						for (String mapping : Arrays.asList(fields).subList(3, fields.length)) {
							namespaceMethodMap.put(this.namespaces.get(i), mapping);
							i++;
						}

						ClassMapping parentClass = this.mapping.getClassByFromName(fields[1]);
						parentClass.methodMappings.add(new MethodMapping(namespaceMethodMap.get(fromNamespace), fields[2], namespaceMethodMap.get(toNamespace), fields[2], parentClass));
						break;
					}
				}
			}

			lineNumber++;
		}

		return this.mapping;
	}
}

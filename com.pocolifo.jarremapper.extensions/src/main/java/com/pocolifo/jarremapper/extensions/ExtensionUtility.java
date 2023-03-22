package com.pocolifo.jarremapper.extensions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.pocolifo.jarremapper.Utility;

public class ExtensionUtility {
	private static Map<String, String> getEnvironment() {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		return env;
	}

	private static FileSystem getFileSystem(File targetFile) throws IOException {
		URI uri = URI.create("jar:" + targetFile.toURI());
		return FileSystems.newFileSystem(uri, getEnvironment());
	}

	public static void copyResources(File inputFile, File outputFile) throws IOException {
		try (FileSystem fileSystem = getFileSystem(outputFile)) {
			try (ZipInputStream stream = new ZipInputStream(new FileInputStream(inputFile))) {
				for (ZipEntry entry; (entry = stream.getNextEntry()) != null;) {
					if (entry.isDirectory()) continue;
					if (entry.getName().endsWith(".class")) continue;

					Path path = fileSystem.getPath(entry.getName());

					if (path.getParent() != null) {
						Files.createDirectories(path.getParent());
					}

					Files.write(path, Utility.readInputStream(stream));
				}
			}
		}
	}

	private static void recursivelyRemovePath(Path path) throws IOException {
		if (!Files.isDirectory(path)) return;

		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
				return Files.deleteIfExists(path) ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
				return Files.deleteIfExists(path) ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
			}
		});
	}

	public static void removeMetaInf(File file) throws IOException {
		try (FileSystem fileSystem = getFileSystem(file)) {
			recursivelyRemovePath(fileSystem.getPath("META-INF"));
		}
	}
}

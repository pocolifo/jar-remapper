package com.pocolifo.jarremapper.devplugin;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IOUtility {
	public static byte[] readInputStream(InputStream stream) throws IOException {
		try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];

			for (int l; (l = stream.read(buffer)) != -1;) {
				byteStream.write(buffer, 0, l);
			}

			return byteStream.toByteArray();
		}
	}

	public static byte[] download(URL url) throws IOException {
		return readInputStream(url.openStream());
	}

	public static void download(URL url, File output, boolean overwrite) throws IOException {
		System.out.println("Download to " + output.getAbsolutePath());
		if (overwrite) Files.deleteIfExists(output.toPath());
		if (!output.exists()) Files.write(output.toPath(), download(url));
	}

	public static void extract(File zipFile, Function<String, String> getFileName, String... fileNames) throws IOException {
		assert zipFile.isFile();

		try (ZipInputStream stream = new ZipInputStream(new FileInputStream(zipFile))) {
			for (ZipEntry entry; (entry = stream.getNextEntry()) != null;) {
				for (String name : fileNames) {
					if (name.endsWith(entry.getName())) {
						copyFile(stream, new File(zipFile.getParentFile(), getFileName.apply(getFileName(entry.getName()))));
					}
				}
			}
		}
	}

	public static void copyResource(String resource, File output) throws IOException {
		copyFile(getResource(resource), output);
	}

	public static void copyFile(InputStream stream, File output) throws IOException {
		Files.write(output.toPath(), readInputStream(stream));
	}

	public static String getFileName(String name) {
		if (!name.contains("/")) return name;

		return name.substring(name.lastIndexOf('/') + 1);
	}

	public static byte[] readResource(String resource) throws IOException {
		return readInputStream(getResource(resource));
	}

	private static InputStream getResource(String resource) {
		return Objects.requireNonNull(IOUtility.class.getResourceAsStream("/" + resource));
	}

	public static void delete(File... files) throws IOException {
		for (File file : files) {
			Files.deleteIfExists(file.toPath());
		}
	}

	public static void deleteDirectory(File directory) throws IOException {
		assert directory.isDirectory();

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else if (file.isFile()) {
				file.delete();
			}
		}

		directory.delete();
	}

	public static URL asUrl(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}

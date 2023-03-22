/*
 * This is an automatically generated test class.
 * The next time you run the 'generateTests' task, this class will be overwritten.
 */

package autogen;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import com.pocolifo.jarremapper.JarRemapper;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;
import com.pocolifo.jarremapper.reader.mcp.McpMappingReader;
import com.pocolifo.jarremapper.reader.tiny.Tiny1MappingReader;
import com.pocolifo.jarremapper.reader.tiny.Tiny2MappingReader;

public class TestUtility {
	public static final File DEFAULT_OUTPUT_FILE = new File("output.jar");

	public static File getResourceAsFile(String resource) {
		try {
			return new File(TestUtility.class.getResource(resource).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JarMapping readMapping(String path) throws IOException {
		File f = getResourceAsFile(path);

		if (f.isDirectory()) {
			return readMcp(path);
		} else {
			try (BufferedReader reader = Files.newBufferedReader(f.toPath())) {
				String firstLine = reader.readLine();

				if (firstLine.startsWith("v1")) {
					return readTiny1(path);
				} else {
					return readTiny2(path);
				}
			}
		}
	}

	public static JarMapping readMcp(String mcpDirectoryPath) throws IOException {
		File mcpDir = getResourceAsFile(mcpDirectoryPath);

		return new McpMappingReader(
				new File(mcpDir, "joined.srg"),
				new File(mcpDir, "joined.exc"),
				new File(mcpDir, "methods.csv"),
				new File(mcpDir, "fields.csv"),
				new File(mcpDir, "params.csv")
		).read();
	}

	public static JarMapping readTiny1(String mappingPath) throws IOException {
		return new Tiny1MappingReader(getResourceAsFile(mappingPath)).read("official", "named");
	}

	public static JarMapping readTiny2(String mappingPath) throws IOException {
		return new Tiny2MappingReader(getResourceAsFile(mappingPath)).read("official", "named");
	}

	public static void remap(JarMapping mapping, AbstractRemappingEngine engine, File input) throws IOException {
		remap(mapping, engine, input, DEFAULT_OUTPUT_FILE, true);
	}

	public static void remap(JarMapping mapping, AbstractRemappingEngine engine, File input, File output, boolean overwrite) throws IOException {
		JarRemapper remapper = JarRemapper.newRemap()
				.withRemappingEngine(engine)
				.withMappings(mapping)
				.withInputFile(input)
				.withOutputFile(output);

		if (overwrite) remapper.overwriteOutputFile();

		remapper.remap();
	}
}

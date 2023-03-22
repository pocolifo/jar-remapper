package com.pocolifo.jarremapper.engine;

import java.io.File;
import java.io.IOException;

import com.pocolifo.jarremapper.mapping.JarMapping;

public abstract class AbstractRemappingEngine {
	protected File inputFile;
	protected File outputFile;
	protected JarMapping mapping;

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public void setMapping(JarMapping mapping) {
		this.mapping = mapping;
	}

	public abstract void remap() throws IOException;
}

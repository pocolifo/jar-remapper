package com.pocolifo.jarremapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utility {
	public static byte[] readInputStream(InputStream stream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		for (int length; (length = stream.read(buffer)) != -1;) {
			outputStream.write(buffer, 0, length);
		}

		return outputStream.toByteArray();
	}
}

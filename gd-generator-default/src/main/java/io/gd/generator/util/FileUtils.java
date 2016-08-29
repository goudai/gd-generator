package io.gd.generator.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtils {
	
	public static String read(String file) {
		return read(new File(file));
	}

	public static String read(File file) {
		if(!file.exists()) {
			return null;
		}
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			StringBuilder lines = new StringBuilder();
			bufferedReader.lines().forEach((line) -> {
				lines.append(line + "\r\n");
			});
			return lines.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

package io.gd.generator.context;

import io.gd.generator.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class GenLog {

	public File file;

	public StringBuffer sb;

	public GenLog(String genLog) throws IOException {
		sb = new StringBuffer("\r\n" + "--" + LocalDateTime.now());
		if (StringUtils.isBank(genLog)) {
			genLog = System.getProperty("user.home") + File.separator + "gd-generator-" + LocalDateTime.now().toString().replace(":", "") + ".log";
		}
		file = new File(genLog);
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public void info(String sql) {
		if (sql.endsWith(";"))
			sb.append("\r\n" + sql);
		else
			sb.append("\r\n" + sql + ";");
	}

	public void warn(String message) {
		sb.append("\r\n" + "--" + message);
	}

	public void flush() throws FileNotFoundException, IOException {
		try (FileOutputStream fos = new FileOutputStream(file, true)) {
			fos.write(sb.toString().getBytes());
		}
	}
}

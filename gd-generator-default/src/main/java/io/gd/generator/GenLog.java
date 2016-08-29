package io.gd.generator;

import io.gd.generator.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenLog {

	static final Logger logger = LoggerFactory.getLogger(GenLog.class);

	public File file;

	public StringBuffer sb;

	public GenLog(String genLog) throws IOException {
		sb = new StringBuffer();
		if (StringUtils.isBlank(genLog)) {
			genLog = System.getProperty("user.home") + File.separator + "gd-generator-" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".log";
		}
		file = new File(genLog);
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public void info(String sql) {
		if (sql.endsWith(";"))
			sb.append("\r\n\r\n" + sql);
		else
			sb.append("\r\n\r\n" + sql + ";");
	}

	public void warn(String message) {
		sb.append("\r\n\r\n" + "--" + message);
	}

	public void flush() throws FileNotFoundException, IOException {
		
		try (FileOutputStream fos = new FileOutputStream(file, true)) {
			fos.write(("\r\n\r\n\r\n\r\n" + "--" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).getBytes());
			fos.write(sb.toString().getBytes());
		}
	}
}

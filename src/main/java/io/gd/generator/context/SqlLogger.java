package io.gd.generator.context;

import io.gd.generator.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class SqlLogger {

	public File file;
	
	public StringBuilder sb;

	public SqlLogger(String sqlLogFile) {
		sb = new StringBuilder("\r\n").append("--" + LocalDateTime.now());
		if(StringUtils.isBank(sqlLogFile)) {
			sqlLogFile = System.getProperty("user.home") + File.separator + "gd-generator-" + LocalDateTime.now().toString().replace(":", "") + ".log";
		}
		file = new File(sqlLogFile);
	}
	
	public void info(String sql) {
		if (sql.endsWith(";"))
			sb.append("\r\n").append(sql);
		else
			sb.append("\r\n").append(sql).append(";");
	}

	public void warn(String message) {
		sb.append("\r\n").append("--" + message);
	}
	

	public void flush() throws FileNotFoundException, IOException {
		if(!file.exists()) {
			file.mkdirs();
			file.createNewFile();
		}
		try (FileOutputStream fos = new FileOutputStream(file, true)) {
			fos.write(sb.toString().getBytes());
		}
	}
}

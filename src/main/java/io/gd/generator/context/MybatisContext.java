package io.gd.generator.context;

import java.io.File;

public class MybatisContext extends JdbcContext {

	private String xmlPath;

	private String mapperPath;

	private File mapperFile;

	private File xmlFile;

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public String getMapperPath() {
		return mapperPath;
	}

	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}

	public File getMapperFile() {
		return mapperFile;
	}

	public void setMapperFile(File mapperFile) {
		this.mapperFile = mapperFile;
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

}

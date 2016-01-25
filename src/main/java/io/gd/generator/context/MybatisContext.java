package io.gd.generator.context;

import java.io.File;
import java.sql.Connection;

public class MybatisContext extends Context {

	private File mapperFie;

	private File xmlFIle;

	private Connection connection;

	public File getMapperFie() {
		return mapperFie;
	}

	public void setMapperFie(File mapperFie) {
		this.mapperFie = mapperFie;
	}

	public File getXmlFIle() {
		return xmlFIle;
	}

	public void setXmlFIle(File xmlFIle) {
		this.xmlFIle = xmlFIle;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}

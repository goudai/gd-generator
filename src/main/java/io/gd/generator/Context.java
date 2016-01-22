package io.gd.generator;

import java.io.File;

import freemarker.template.Configuration;

public class Context {
	
	private enum Status {
		NEW, READ, PARSED, MERGED, WRITTEN;
	}

	private Config config;

	private Class<?> entityClass;

	private Class<?> queryModelClass;

	private Configuration freemarkerConfiguration;
	
	private File destFile;
	
	private Status status;
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public File getDestFile() {
		return destFile;
	}

	public void setDestFile(File destFile) {
		this.destFile = destFile;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<?> getQueryModelClass() {
		return queryModelClass;
	}

	public void setQueryModelClass(Class<?> queryModelClass) {
		this.queryModelClass = queryModelClass;
	}

	public Configuration getFreemarkerConfiguration() {
		return freemarkerConfiguration;
	}

	public void setFreemarkerConfiguration(Configuration freemarkerConfiguration) {
		this.freemarkerConfiguration = freemarkerConfiguration;
	}

}

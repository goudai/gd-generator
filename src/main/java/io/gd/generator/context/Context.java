package io.gd.generator.context;

import freemarker.template.Configuration;
import io.gd.generator.config.Config;

public abstract class Context {

	protected Config config;

	protected Class<?> entityClass;

	protected Configuration freemarkerConfiguration;

	protected GenLog genLog;

	public GenLog getGenLog() {
		return genLog;
	}

	public void setGenLog(GenLog genLog) {
		this.genLog = genLog;
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

	public Configuration getFreemarkerConfiguration() {
		return freemarkerConfiguration;
	}

	public void setFreemarkerConfiguration(Configuration freemarkerConfiguration) {
		this.freemarkerConfiguration = freemarkerConfiguration;
	}

}

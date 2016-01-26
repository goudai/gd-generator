package io.gd.generator.config;

public class Config {

	private String queryModelSuffix = "QueryModel";

	private String genLogFile;

	private String entityPackage;
	private String queryModelPackage;

	private String mybatisMapperPackage;
	private String mybatisXmlPackage;

	private String mybatisMapperPath;
	private String mybatisXmlPath;

	private String url = "jdbc:mysql://127.0.0.1/test";
	private String username = "root";
	private String password;

	private String javaSrc = "/src/main/java";
	private String resources = "/src/main/resources";

	private String freemakerVersion = "2.3.0";
	private String template = "io/gd/generator/template";
	private String defaultEncoding = "UTF-8";

	public String getGenLogFile() {
		return genLogFile;
	}

	public void setGenLogFile(String genLogFile) {
		this.genLogFile = genLogFile;
	}

	public String getFreemakerVersion() {
		return freemakerVersion;
	}

	public void setFreemakerVersion(String freemakerVersion) {
		this.freemakerVersion = freemakerVersion;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getMybatisMapperPackage() {
		return mybatisMapperPackage;
	}

	public void setMybatisMapperPackage(String mybatisMapperPackage) {
		this.mybatisMapperPackage = mybatisMapperPackage;
	}

	public String getMybatisXmlPackage() {
		return mybatisXmlPackage;
	}

	public void setMybatisXmlPackage(String mybatisXmlPackage) {
		this.mybatisXmlPackage = mybatisXmlPackage;
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	public String getQueryModelPackage() {
		return queryModelPackage;
	}

	public void setQueryModelPackage(String queryModelPackage) {
		this.queryModelPackage = queryModelPackage;
	}

	public String getMybatisMapperPath() {
		return mybatisMapperPath;
	}

	public void setMybatisMapperPath(String mybatisMapperPath) {
		this.mybatisMapperPath = mybatisMapperPath;
	}

	public String getMybatisXmlPath() {
		return mybatisXmlPath;
	}

	public void setMybatisXmlPath(String mybatisXmlPath) {
		this.mybatisXmlPath = mybatisXmlPath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJavaSrc() {
		return javaSrc;
	}

	public void setJavaSrc(String javaSrc) {
		this.javaSrc = javaSrc;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

	public String getQueryModelSuffix() {
		return queryModelSuffix;
	}

	public void setQueryModelSuffix(String queryModelSuffix) {
		this.queryModelSuffix = queryModelSuffix;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

}

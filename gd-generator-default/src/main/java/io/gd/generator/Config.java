package io.gd.generator;

public class Config {

	private boolean useLombok = true;

	private String genLogFile;

	private String entityPackage;

	private boolean useEnumOrdinalTypeHandlerByDefault = true;
	private boolean escapeColumn = false;

	/* java query model */
	private String queryModelPackage;
	private String queryModelPath;
	private String queryModelSuffix = "QueryModel";

	/* mybatis */
	private String mybatisMapperPackage;
	private String mybatisMapperPath;
	private String mybatisXmlPath;

	/* mysql */
	private String url = "jdbc:mysql://127.0.0.1/test";
	private String username = "root";
	private String password = "123456";

	private String freemakerVersion = "2.3.0";
	private String template = "io/gd/generator/template";
	private String defaultEncoding = "UTF-8";

	/* nodejs */
	private String nodeDestFile;
	private String nodeDocFile;
	private String nodeServicePackage;

	public String getNodeDestFile() {
		return nodeDestFile;
	}

	public void setNodeDestFile(String nodeDestFile) {
		this.nodeDestFile = nodeDestFile;
	}

	public String getNodeDocFile() {
		return nodeDocFile;
	}

	public void setNodeDocFile(String nodeDocFile) {
		this.nodeDocFile = nodeDocFile;
	}

	public String getNodeServicePackage() {
		return nodeServicePackage;
	}

	public void setNodeServicePackage(String nodeServicePackage) {
		this.nodeServicePackage = nodeServicePackage;
	}

	public boolean isUseLombok() {
		return useLombok;
	}

	public void setUseLombok(boolean useLombok) {
		this.useLombok = useLombok;
	}

	public String getQueryModelPath() {
		return queryModelPath;
	}

	public void setQueryModelPath(String queryModelPath) {
		this.queryModelPath = queryModelPath;
	}

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

	public boolean isUseEnumOrdinalTypeHandlerByDefault() {
		return useEnumOrdinalTypeHandlerByDefault;
	}

	public void setUseEnumOrdinalTypeHandlerByDefault(boolean useEnumOrdinalTypeHandlerByDefault) {
		this.useEnumOrdinalTypeHandlerByDefault = useEnumOrdinalTypeHandlerByDefault;
	}

	public boolean isEscapeColumn() {
		return escapeColumn;
	}

	public void setEscapeColumn(boolean escapeColumn) {
		this.escapeColumn = escapeColumn;
	}
}

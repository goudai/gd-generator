package io.gd.generator.meta.mybatis.xml;

public class MybatisMappingMeta {
	private String column;
	private String property;
	private String enumHander;
	private String javaType;

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getEnumHander() {
		return enumHander;
	}

	public void setEnumHander(String enumHander) {
		this.enumHander = enumHander;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

}

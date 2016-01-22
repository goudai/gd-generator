package io.gd.generator.meta.mybatis.xml;

public class MybatisMappingMeta {
	private String column;
	private String property;
	private String enumHander;
	private String javaType;

	public String getEnumHander() {
		return enumHander;
	}

	public MybatisMappingMeta setEnumHander(String enumHander) {
		this.enumHander = enumHander;
		return this;
	}

	public String getColumn() {
		return column;
	}

	public MybatisMappingMeta setColumn(String column) {
		this.column = column;
		return this;
	}

	public String getProperty() {
		return property;
	}

	public MybatisMappingMeta setProperty(String property) {
		this.property = property;
		return this;
	}

	public static MybatisMappingMeta newMybatisMappingMeta() {
		return new MybatisMappingMeta();
	}
	

	public String getJavaType() {
		return javaType;
	}

	public MybatisMappingMeta setJavaType(String javaType) {
		this.javaType = javaType;
		return this;
	}

	@Override
	public String toString() {
		return "MybatisMappingMeta [column=" + column + ", property="
				+ property + "]";
	}

}

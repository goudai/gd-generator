package io.gd.generator.meta.mysql;

public class MysqlColumnMeta {

	private String name;
	private String type;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public MysqlColumnMeta setName(String name) {
		this.name = name;
		return this;
	}

	public MysqlColumnMeta setType(String type) {
		this.type = type;
		return this;
	}

	public static MysqlColumnMeta newMysqlColumnMeta() {
		return new MysqlColumnMeta();
	}

	@Override
	public String toString() {
		return "`"+name+"`" + " " + type;
	}

}

package io.gd.generator.meta.mysql;

import java.util.ArrayList;
import java.util.List;

public class MysqlTableMeta {

	private String table;
	private Class<?> klass;

	private List<MysqlColumnMeta> mysqlColumnMetas = new ArrayList<MysqlColumnMeta>();
	private List<String> uniques = new ArrayList<String>();

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Class<?> getKlass() {
		return klass;
	}

	public void setKlass(Class<?> klass) {
		this.klass = klass;
	}

	public List<MysqlColumnMeta> getMysqlColumnMetas() {
		return mysqlColumnMetas;
	}

	public void setMysqlColumnMetas(List<MysqlColumnMeta> mysqlColumnMetas) {
		this.mysqlColumnMetas = mysqlColumnMetas;
	}

	public List<String> getUniques() {
		return uniques;
	}

	public void setUniques(List<String> uniques) {
		this.uniques = uniques;
	}

}

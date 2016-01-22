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

	public List<MysqlColumnMeta> getMysqlColumnMetas() {
		return mysqlColumnMetas;
	}

	public MysqlTableMeta setTable(String table) {
		this.table = table;
		return this;
	}

	public MysqlTableMeta setMysqlColumnMetas(List<MysqlColumnMeta> columnMetas) {
		this.mysqlColumnMetas = columnMetas;
		return this;
	}

	public Class<?> getKlass() {
		return klass;
	}

	public MysqlTableMeta setKlass(Class<?> klass) {
		this.klass = klass;
		return this;
	}

	public static MysqlTableMeta newMysqlTableMeta() {
		return new MysqlTableMeta();
	}

	@Override
	public String toString() {
		return mysqlColumnMetas.toString().replace("]", "").replace("[", "");
	}

	public List<String> getUniques() {
		return uniques;
	}

	public void setUniques(List<String> uniques) {
		this.uniques = uniques;
	}
	
	

}

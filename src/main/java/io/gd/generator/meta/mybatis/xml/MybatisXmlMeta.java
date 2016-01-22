package io.gd.generator.meta.mybatis.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisXmlMeta {

	private String mapperName;
	private String model;
	private String table;
	private String simpleName;
	private String query;
	private String version = "";
	private Map<String, String> querys = new HashMap<String,String>();
	
	private List<MybatisMappingMeta> mappingMetas = new ArrayList<MybatisMappingMeta>();
	
	public String getMapperName() {
		return mapperName;
	}
	public MybatisXmlMeta setMapperName(String mapperName) {
		this.mapperName = mapperName;
		return this;
	}
	
	public String getModel() {
		return model;
	}
	public MybatisXmlMeta setModel(String model) {
		this.model = model;
		return this;
	}
	
	public String getTable() {
		return table;
	}
	public MybatisXmlMeta setTable(String table) {
		this.table = table;
		return this;
	}
	public List<MybatisMappingMeta> getMappingMetas() {
		return mappingMetas;
	}
	public void setMappingMetas(List<MybatisMappingMeta> mappingMetas) {
		this.mappingMetas = mappingMetas;
	}
	public static MybatisXmlMeta newMybatisXmlMeta(){
		return new MybatisXmlMeta();
	}
	
	

	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public Map<String, String> getQuerys() {
		return querys;
	}
	public void setQuerys(Map<String, String> querys) {
		this.querys = querys;
	}
	
	public String getSimpleName() {
		return simpleName;
	}
	public MybatisXmlMeta setSimpleName(String simpleName) {
		this.simpleName = simpleName;
		return this;
	}
	@Override
	public String toString() {
		return "MybatisXmlMeta [mapperName=" + mapperName 
				+ ", mappingMetas=" + mappingMetas + "]";
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
	
	
}

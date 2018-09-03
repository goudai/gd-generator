package io.gd.generator.meta.mybatis;

import java.util.ArrayList;
import java.util.List;

public class MybatisMapperMeta {

	private String mapperPackage;

	private String entityName;
	private String idType;
	private String entitySimpleName;
	private String queryModelName;
	private String queryModelSimpleName;
	private boolean hasQueryModel;
	private List<String> otherMethods = new ArrayList<>(); // 其他方法
	private List<String> otherImports = new ArrayList<>(); // 其他引包
	private String idPropName;

	public String getIdPropName() {
		return idPropName;
	}

	public void setIdPropName(String idPropName) {
		this.idPropName = idPropName;
	}

	public String getMapperPackage() {
		return mapperPackage;
	}

	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntitySimpleName() {
		return entitySimpleName;
	}

	public void setEntitySimpleName(String entitySimpleName) {
		this.entitySimpleName = entitySimpleName;
	}

	public String getQueryModelName() {
		return queryModelName;
	}

	public void setQueryModelName(String queryModelName) {
		this.queryModelName = queryModelName;
	}

	public String getQueryModelSimpleName() {
		return queryModelSimpleName;
	}

	public void setQueryModelSimpleName(String queryModelSimpleName) {
		this.queryModelSimpleName = queryModelSimpleName;
	}

	public boolean isHasQueryModel() {
		return hasQueryModel;
	}

	public void setHasQueryModel(boolean hasQueryModel) {
		this.hasQueryModel = hasQueryModel;
	}

	public List<String> getOtherMethods() {
		return otherMethods;
	}

	public void setOtherMethods(List<String> otherMethods) {
		this.otherMethods = otherMethods;
	}

	public List<String> getOtherImports() {
		return otherImports;
	}

	public void setOtherImports(List<String> otherImports) {
		this.otherImports = otherImports;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}
}

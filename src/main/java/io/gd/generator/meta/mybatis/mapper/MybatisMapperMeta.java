package io.gd.generator.meta.mybatis.mapper;

import java.util.List;

public class MybatisMapperMeta {

	private String entityName;
	private String entitySimpleName;
	private String queryModelName;
	private String queryModelSimpleName;
	private boolean hasQueryModel;
	private List<String> otherMethods; // 其他方法
	private List<String> otherImports; // 其他引包

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

}

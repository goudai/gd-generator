package io.gd.generator.meta.mybatis.mapper;

import java.util.List;

public class MybatisMapperMeta {
	private String modelName;
	private String simpleName;
	private String queryModel;
	private String simpleQuery;
	private boolean hasQueryModel;
	private List<String> otherMethods; // 其他方法
	private List<String> otherImports; // 其他引包

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getQueryModel() {
		return queryModel;
	}

	public void setQueryModel(String queryModel) {
		this.queryModel = queryModel;
	}

	public String getSimpleQuery() {
		return simpleQuery;
	}

	public void setSimpleQuery(String simpleQuery) {
		this.simpleQuery = simpleQuery;
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

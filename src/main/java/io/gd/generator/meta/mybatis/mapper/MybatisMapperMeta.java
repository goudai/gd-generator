package io.gd.generator.meta.mybatis.mapper;

import java.util.ArrayList;
import java.util.List;

public class MybatisMapperMeta {
	private String modelName;
	private String simpleName;
	private String queryModel;
	private String simpleQuery;
	
	private List<String> otherMethods = new ArrayList<>();

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

}

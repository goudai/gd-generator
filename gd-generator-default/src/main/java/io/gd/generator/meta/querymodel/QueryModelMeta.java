package io.gd.generator.meta.querymodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryModelMeta {

	private boolean useLombok;

	private String queryModelPackage;

	private String type;

	private Set<String> fieldNames = new HashSet<>();

	private List<QueryModelField> queryModelFields = new ArrayList<>();

	private Set<String> importFullTypes = new HashSet<>(); // import全名

	public Set<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(Set<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public boolean isUseLombok() {
		return useLombok;
	}

	public void setUseLombok(boolean useLombok) {
		this.useLombok = useLombok;
	}

	public Set<String> getImportFullTypes() {
		return importFullTypes;
	}

	public void setImportFullTypes(Set<String> importFullTypes) {
		this.importFullTypes = importFullTypes;
	}

	public List<QueryModelField> getQueryModelFields() {
		return queryModelFields;
	}

	public void setQueryModelFields(List<QueryModelField> queryModelFields) {
		this.queryModelFields = queryModelFields;
	}

	public String getQueryModelPackage() {
		return queryModelPackage;
	}

	public void setQueryModelPackage(String queryModelPackage) {
		this.queryModelPackage = queryModelPackage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static class QueryModelField {

		private String name;

		private String type;

		private boolean array;

		public boolean isArray() {
			return array;
		}

		public void setArray(boolean array) {
			this.array = array;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

}

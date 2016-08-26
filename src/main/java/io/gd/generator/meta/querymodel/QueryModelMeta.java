package io.gd.generator.meta.querymodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryModelMeta {

	private String name;

	private List<QueryModelField> queryModelFields = new ArrayList<>();

	public static class QueryModelField {

		private String name;

		private Set<String> suffixs = new HashSet<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Set<String> getSuffixs() {
			return suffixs;
		}

		public void setSuffixs(Set<String> suffixs) {
			this.suffixs = suffixs;
		}

	}

	public List<QueryModelField> getQueryModelFields() {
		return queryModelFields;
	}

	public void setQueryModelFields(List<QueryModelField> queryModelFields) {
		this.queryModelFields = queryModelFields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

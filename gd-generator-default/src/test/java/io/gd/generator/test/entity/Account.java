package io.gd.generator.test.entity;

import io.gd.generator.annotation.Type;
import io.gd.generator.annotation.query.Query;
import io.gd.generator.annotation.query.QueryModel;
import io.gd.generator.api.query.Predicate;

import javax.persistence.Table;

/**
 * Created by freeman on 16/6/21.
 */
@QueryModel
@Table
public class Account {

	@Query(value = {Predicate.EQ})
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}



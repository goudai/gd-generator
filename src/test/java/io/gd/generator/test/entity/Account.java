package io.gd.generator.test.entity;

import io.gd.generator.api.query.Predicate;
import io.gd.generator.api.query.Query;
import io.gd.generator.api.query.QueryModel;

/**
 * Created by freeman on 16/6/21.
 */
@QueryModel
public class Account{

	@Query(predicate = {Predicate.EQ})
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}



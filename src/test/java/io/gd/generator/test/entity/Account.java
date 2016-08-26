package io.gd.generator.test.entity;

import io.gd.generator.api.Predicate;
import io.gd.generator.api.Query;
import io.gd.generator.api.QueryModel;

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



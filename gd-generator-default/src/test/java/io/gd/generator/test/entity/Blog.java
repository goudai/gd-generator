package io.gd.generator.test.entity;

import io.gd.generator.annotation.Field;
import io.gd.generator.annotation.Type;
import io.gd.generator.annotation.query.Query;
import io.gd.generator.annotation.query.QueryModel;
import io.gd.generator.annotation.view.View;
import io.gd.generator.annotation.view.ViewObject;
import io.gd.generator.api.query.Predicate;

@QueryModel
@ViewObject(groups = {"BlogVo", "UserBlogVo"})
@Type(label = "博客")
public class Blog {

	@Query(value = {Predicate.EQ})
	@View(groups = {"UserBlogVo"}, name = "titleLabel", type = String.class)
	@Field(label = "标题")
	private String title;

	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

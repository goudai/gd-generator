package io.gd.generator.test.entity;

import io.gd.generator.api.Field;
import io.gd.generator.api.Type;
import io.gd.generator.api.query.Predicate;
import io.gd.generator.api.query.Query;
import io.gd.generator.api.query.QueryModel;
import io.gd.generator.api.vo.View;
import io.gd.generator.api.vo.ViewObject;

@QueryModel
@ViewObject(groups = {"BlogVo", "UserBlogVo"})
@Type(name = "博客")
public class Blog {

	@Query(value = { Predicate.EQ })
	@View(group = {"UserBlogVo"}, name = "titleLabel", type = String.class)
	@Field(name = "标题")
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

package io.gd.generator.test.entity;

import io.gd.generator.api.query.Predicate;
import io.gd.generator.api.query.Query;
import io.gd.generator.api.query.QueryModel;
import io.gd.generator.api.vo.View;
import io.gd.generator.api.vo.ViewObject;

/**
 * Created by freeman on 16/6/21.
 */
@QueryModel
@ViewObject(groups = {"BlogVo", "UserBlogVo"})
public class Blog {

	@Query(predicate = { Predicate.EQ })
	@View(group = {"UserBlogVo"}, name = "titleLabel", type = String.class)
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

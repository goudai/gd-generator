package io.gd.generator.test.entity;

import io.gd.generator.annotation.Default;
import io.gd.generator.annotation.Field;
import io.gd.generator.annotation.query.Query;
import io.gd.generator.api.query.Predicate;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "demo",schema="测试表")
public class Demo {
    @Id
    private Long id;

    @Default("0")
    private Integer number;
    @NotEmpty
    @Field(label = "标题")
    private String title;
    @NotNull
    @Field(label = "内容")
    @Column(columnDefinition = "text")
    private String content;
    @NotBlank
    @Field(label = "说明")
    @Lob
    private String illustration;
    @Field(label = "创建日期")
    @Default("2019-12-26")
    private Date createtime;

    @Field(label = "更新时间")
    @Default(type = Default.DefaultType.DBKEY,value = Default.CURRENT_TIMESTAMP_ONUPDATE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatetime;

    @Query(Predicate.EQ)
    @Field(label = "是否有效")
    private Boolean valid;
}

package io.gd.generator.test.entity;

import io.gd.generator.annotation.Default;
import io.gd.generator.annotation.Field;

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
    @NotNull
    @Field(label = "标题")
    private String title;
    @NotNull
    @Field(label = "内容")
    @Column(columnDefinition = "text")
    private String content;

    @Field(label = "创建日期")
    @Default("2019-12-26")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createtime;

    @Field(label = "更新时间")
    @Default(type = Default.DefaultType.DBKEY,value = Default.CURRENT_TIMESTAMP_ONUPDATE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatetime;
}

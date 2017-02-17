<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${meta.mapperName}" >
<#assign rep="#">
<#assign rep$="$">
<#assign baseColumn><#list meta.mappingMetas as br><#if br.column != 'id'><#if br_has_next>${br.column},<#else>${br.column}</#if></#if></#list><#if meta.version??>,version</#if></#assign>
<#assign baseProperty><#list meta.mappingMetas as br><#if br.property != 'id'><#if br_has_next><#if br.enumHander??>${rep}{${br.property},typeHandler=${br.enumHander}},<#else>${rep}{${br.property}},</#if><#else><#if br.enumHander??>${rep}{${br.property},typeHandler=${br.enumHander}}<#else>${rep}{${br.property}}</#if></#if></#if></#list></#assign>

	<resultMap id="baseResultMap" type="${meta.model}">
		<#list meta.mappingMetas as br>
		<#if br.column == 'id'>
		<id column="${br.column}" property="${br.property}" />
		<#else>
		<#if br.enumHander??>
		<result column="${br.column}" property="${br.property}" typeHandler="${br.enumHander}" />
		<#else>
		<result column="${br.column}" property="${br.property}" />
		</#if>
		</#if>
		</#list>
		<#if meta.version??>
		<result column="version" property="version" />
		</#if>
	</resultMap>

	<sql id="baseColumn">id,${baseColumn}</sql>

	<insert id="insert" parameterType="${meta.model}"<#if meta.useGeneratedKeys> useGeneratedKeys="true" keyProperty="id"</#if>>
	  insert into `${meta.table?trim}` (<#if !meta.useGeneratedKeys>id,</#if>${baseColumn})
	  values (${baseProperty}<#if meta.version??>,${rep}{${meta.version}}</#if>)
	</insert>

	<delete id="delete">
		delete from `${meta.table?trim}` where id = ${rep}{id}
	</delete>

	<update id="update" parameterType="${meta.model}">
		update `${meta.table?trim}`
		<set>
		<#list meta.mappingMetas as br>
		<#if br.property != 'id'>
		<#if br.enumHander??>
			${br.column} = ${rep}{${br.property},typeHandler=${br.enumHander}},
		<#else>
			${br.column} = ${rep}{${br.property}},
		</#if>
		</#if>
		</#list>
		<#if meta.version??>
			${meta.version} = ${meta.version} + 1,
		</#if>
		</set>
		where id = ${rep}{id}<#if meta.version??> and ${meta.version}=${rep}{${meta.version}}</#if>
	</update>

	<update id="merge">
		update `${meta.table?trim}`
			<set>
				<foreach collection="fields" item="field">
				<choose>
				<#list meta.mappingMetas as br>
				<#if br.property != 'id'>
					<#if br.enumHander??>
					<when test="field == '${br.property}'">${br.column} = ${rep}{${meta.simpleName?uncap_first}.${br.property},typeHandler=${br.enumHander},javaType=${br.javaType}},</when>
					<#else>
					<when test="field == '${br.property}'">${br.column} = ${rep}{${meta.simpleName?uncap_first}.${br.property}},</when>
					</#if>
				</#if>
				</#list>
				</choose>
				</foreach>
			</set>
		where id = ${rep}{${meta.simpleName?uncap_first}.id}
	</update>

	<select id="findOne" resultMap="baseResultMap" parameterType="long">
		select
		<include refid="baseColumn"/>
		from `${meta.table?trim}`
		where id = ${rep}{id}
	</select>
	<#if !meta.hasQueryModel>

	<select id="findAll" resultMap="baseResultMap">
		select
		<include refid="baseColumn"/>
		from `${meta.table?trim}`
		order by id desc
	</select>
	</#if>
	<#if meta.hasQueryModel>

	<select id="findAll" resultMap="baseResultMap" parameterType="${meta.query}">
		select
		<include refid="baseColumn"/>
		from `${meta.table?trim}`
		<where>
			<#list meta.querys?keys as key>
			<if test="${key} != null">
				${meta.querys[key]}
			</if>
			</#list>
		</where>
		<choose>
			<when test="orderByAndDirection != null">
				order by ${rep$}{orderByAndDirection}
			</when>
			<otherwise>
				order by id desc
			</otherwise>
		</choose>
		<if test="offset != null">
			limit ${rep}{offset}, ${rep}{pageSize}
		</if>
	</select>

	<select id="count" resultType="_long" parameterType="${meta.query}">
		select count(*) from `${meta.table?trim}`
		<where>
			<#list meta.querys?keys as key>
			<if test="${key} != null">
				${meta.querys[key]}
			</if>
			</#list>
		</where>
	</select>
	</#if>
	<#if meta.otherMappings??>
	<#list meta.otherMappings as otherMapping>

	${otherMapping}
	</#list>
	</#if>

</mapper>
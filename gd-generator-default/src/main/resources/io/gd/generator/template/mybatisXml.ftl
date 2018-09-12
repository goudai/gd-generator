<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${meta.mapperName}" >
<#assign rep="#">
<#assign rep$="$">
<#assign baseColumn><#list meta.mappingMetas as br><#if br.column != "${meta.idColumnName}"><#if br_has_next>${br.column},<#else>${br.column}</#if></#if></#list><#if meta.version??>,version</#if></#assign>
<#assign baseProperty><#list meta.mappingMetas as br><#if br.property != "${meta.idPropName}"><#if br_has_next><#if br.typeHandler??>${rep}{${br.property},typeHandler=${br.typeHandler}},<#else>${rep}{${br.property}},</#if><#else><#if br.typeHandler??>${rep}{${br.property},typeHandler=${br.typeHandler}}<#else>${rep}{${br.property}}</#if></#if></#if></#list></#assign>

	<resultMap id="baseResultMap" type="${meta.model}">
		<#list meta.mappingMetas as br>
		<#if br.column == "${meta.idColumnName}">
		<id column="${br.rawColumn}" property="${br.property}" />
		<#else>
		<#if br.typeHandler??>
		<result column="${br.rawColumn}" property="${br.property}" typeHandler="${br.typeHandler}" />
		<#else>
		<#if br.jdbcType??>
		<result column="${br.rawColumn}" property="${br.property}" jdbcType="${br.jdbcType}" />
		<#else>
		<result column="${br.rawColumn}" property="${br.property}" />
		</#if>
		</#if>
		</#if>
		</#list>
		<#if meta.version??>
		<result column="version" property="version" />
		</#if>
	</resultMap>

	<sql id="baseColumn">${meta.idColumnName},${baseColumn}</sql>

	<sql id="condition">
	<#list meta.querys?keys as key>
		<if test="${key} != null">
			${meta.querys[key]}
		</if>
	</#list>
	</sql>

	<insert id="insert" parameterType="${meta.model}"<#if meta.useGeneratedKeys> useGeneratedKeys="true" keyProperty="${meta.idPropName}"</#if>>
	  insert into `${meta.table?trim}` (<#if !meta.useGeneratedKeys>${meta.idColumnName},</#if>${baseColumn})
	  values (<#if !meta.useGeneratedKeys>${rep}{${meta.idPropName}},</#if>${baseProperty}<#if meta.version??>,${rep}{${meta.version}}</#if>)
	</insert>

	<delete id="delete">
		delete from `${meta.table?trim}` where ${meta.idColumnName} = ${rep}{${meta.idPropName}}
	</delete>

	<update id="update" parameterType="${meta.model}">
		update `${meta.table?trim}`
		<set>
		<#list meta.mappingMetas as br>
		<#if br.property != "${meta.idPropName}">
		<#if br.typeHandler??>
			${br.column} = ${rep}{${br.property},typeHandler=${br.typeHandler}},
		<#else>
		<#if br.jdbcType??>
			${br.column} = ${rep}{${br.property},jdbcType=${br.jdbcType}},
		<#else>
			${br.column} = ${rep}{${br.property}},
		</#if>
		</#if>
		</#if>
		</#list>
		<#if meta.version??>
			${meta.version} = ${meta.version} + 1,
		</#if>
		</set>
		where ${meta.idColumnName} = ${rep}{${meta.idPropName}}<#if meta.version??> and ${meta.version}=${rep}{${meta.version}}</#if>
	</update>

	<update id="merge">
		update `${meta.table?trim}`
			<set>
				<foreach collection="fields" item="field">
				<choose>
				<#list meta.mappingMetas as br>
				<#if br.property != "${meta.idPropName}">
					<#if br.typeHandler??>
					<when test="field == '${br.property}'">${br.column} = ${rep}{${meta.simpleName?uncap_first}.${br.property},typeHandler=${br.typeHandler},javaType=${br.javaType}},</when>
					<#else>
					<#if br.jdbcType??>
					<when test="field == '${br.property}'">${br.column} = ${rep}{${meta.simpleName?uncap_first}.${br.property},jdbcType=${br.jdbcType}},</when>
					<#else>
					<when test="field == '${br.property}'">${br.column} = ${rep}{${meta.simpleName?uncap_first}.${br.property}},</when>
					</#if>
					</#if>
				</#if>
				</#list>
				</choose>
				</foreach>
			</set>
		where ${meta.idColumnName} = ${rep}{${meta.simpleName?uncap_first}.${meta.idPropName}}
	</update>

	<select id="findOne" resultMap="baseResultMap">
		select
		<include refid="baseColumn"/>
		from `${meta.table?trim}`
		where ${meta.idColumnName} = ${rep}{${meta.idPropName}}
	</select>
	<#if !meta.hasQueryModel>

	<select id="findAll" resultMap="baseResultMap">
		select
		<include refid="baseColumn"/>
		from `${meta.table?trim}`
		order by ${meta.idColumnName} desc
	</select>
	</#if>
	<#if meta.hasQueryModel>

	<select id="findAll" resultMap="baseResultMap" parameterType="${meta.query}">
		select
		<include refid="baseColumn"/>
		from `${meta.table?trim}`
		<where>
			<include refid="condition"/>
		</where>
		<choose>
			<when test="orderByAndDirection != null">
				order by ${rep$}{orderByAndDirection}
			</when>
			<otherwise>
				order by ${meta.idColumnName} desc
			</otherwise>
		</choose>
		<if test="offset != null">
			limit ${rep}{offset}, ${rep}{pageSize}
		</if>
	</select>

	<select id="count" resultType="_long" parameterType="${meta.query}">
		select count(*) from `${meta.table?trim}`
		<where>
            <include refid="condition"/>
		</where>
	</select>
	</#if>
	<#if meta.otherMappings??>
	<#list meta.otherMappings as otherMapping>

	${otherMapping}
	</#list>
	</#if>

</mapper>
package ${basePackage};


import org.apache.ibatis.annotations.Param;

import ${mmm.entityName};
<#if mmm.hasQueryModel>
import java.util.List;
import ${mmm.queryModelName};
</#if>

public interface ${mmm.entitySimpleName}Mapper {

	int insert(${mmm.entitySimpleName} ${mmm.entitySimpleName?uncap_first});

	int update(${mmm.entitySimpleName} ${mmm.entitySimpleName?uncap_first});

	int merge(@Param("${mmm.entitySimpleName?uncap_first}") ${mmm.entitySimpleName} ${mmm.entitySimpleName?uncap_first}, @Param("fields")String... fields);

	int delete(Long id);

	${mmm.entitySimpleName} findOne(Long id);
	<#if mmm.hasQueryModel>

	List<${mmm.entitySimpleName}> findAll(${mmm.queryModelSimpleName} ${mmm.queryModelSimpleName?uncap_first});

	long count(${mmm.queryModelSimpleName} ${mmm.queryModelSimpleName?uncap_first});
	</#if>
	<#if mmm.otherMethods??>
	<#list mmm.otherMethods as otherMethod>

	${otherMethod};
	</#list>
	</#if>

}
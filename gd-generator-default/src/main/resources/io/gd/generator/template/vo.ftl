package ${meta.voPackage};

import io.gd.generator.annotation.Field;
<#if meta.importOther??>
	<#list meta.importOther as import>
import ${import};
	</#list>
</#if>
<#if meta.useLombok>
import lombok.Getter;
import lombok.Setter;
</#if>

import java.io.Serializable;
<#if meta.importJava??>
	<#list meta.importJava as import>
import ${import};
	</#list>
</#if>

<#if meta.useLombok>
@Getter
@Setter
</#if>
public class ${meta.className} implements Serializable {
	/* 原生 */
<#list meta.fields as field>
	@Field(label = "${field.label}"<#if field.order != 999>, order = ${field.order}</#if>)
	private ${field.type} ${field.name};
</#list>

	/* 扩展 */
<#list meta.associationFields as field>
	@Field(label = "${field.label}"<#if field.order != 999>, order = ${field.order}</#if>)
	private ${field.type} ${field.name};
</#list>
<#list meta.collectionFields as field>
	@Field(label = "${field.label}"<#if field.order != 999>, order = ${field.order}</#if>)
	private ${field._interface}${field.elementGroup} ${field.name} = new ${field.type}<>();
</#list>
<#list meta.mapFields as field>
	@Field(label = "${field.label}"<#if field.order != 999>, order = ${field.order}</#if>)
	private ${field._interface}<${field.key},${field.value}> ${field.name} = new ${field.type}<>();
</#list>

}
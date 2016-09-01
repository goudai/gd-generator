package ${meta.voPackage};

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
	private ${field.type} ${field.name};
</#list>

	/* 扩展 */
<#list meta.associationFields as field>
	private ${field.type} ${field.name};
</#list>
<#list meta.collectionFields as field>
	private ${field._interface}${field.elementGroup} ${field.name} = new ${field.type}<>();
</#list>
<#list meta.mapFields as field>
	private ${field._interface}<${field.key},${field.value}> ${field.name} = new ${field.type}<>();
</#list>

}
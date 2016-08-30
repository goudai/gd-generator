package ${meta.voPackage};

<#if meta.useLombok>

import lombok.Getter;
import lombok.Setter;
</#if>

<#if meta.imports??>
	<#list meta.imports as import>
import ${import};
	</#list>
</#if>


<#if meta.useLombok>
@Getter
@Setter
</#if>
public class ${meta.className} {
	/* 原生 */
<#list meta.fields as field>
	private <#if field.paradigm != ''>${field.type}${field.paradigm} ${field.name}= new ${field.type}();<#else>${field.type} ${field.name};</#if>
</#list>

	/* 扩展 */
<#list meta.fields2 as field>
	private <#if field.paradigm != ''>${field.type}${field.paradigm} ${field.name}= new ${field.type}();<#else>${field.type} ${field.name};</#if>
</#list>

}
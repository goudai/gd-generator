package ${meta.voPackage};

<#if meta.useLombok>

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
</#if>

<#if meta.imports??>
	<#list meta.imports as import>
	import ${import};
	</#list>
</#if>


<#if meta.useLombok>
@Getter
@Setter
@Builder
</#if>
public class ${meta.className} {

<#list meta.fields as field>
private <#if field.paradigm != ''>${field.type}${field.paradigm} ${field.name}= new ${field.type}();<#else>${field.type} ${field.name};</#if>
</#list>


}
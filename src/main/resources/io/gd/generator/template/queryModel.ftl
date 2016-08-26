package ${queryModelPackage};


import java.util.List;
import org.apache.ibatis.annotations.Param;

import ${mmm.entityName};
<#if mmm.hasQueryModel>
import ${mmm.queryModelName};
</#if>

<#if mmm.otherImports??>
<#list mmm.otherImports as otherImport>
import ${otherImport};
</#list>
</#if>

public class ${meta.simpleName} {

	</#if>
	<#if meta.otherMethods??>
	<#list meta.otherMethods as otherMethod>

	${otherMethod};
	</#list>
	</#if>

	private Integer pageNumber;

	private Integer pageSize;

	private String orderBy;

	private Direction direction;
	
	public Long getOffset() {
		if (pageNumber == null || pageSize == null) {
			return null;
		}
		return ((long) pageNumber) * pageSize;
	}

	public String getOrderByAndDirection() {
		if (StringUtils.isBlank(orderBy)) {
			return null;
		}
		if (StringUtils.containsAny(orderBy, ';', ',', '\'', '"')) {
			return null;
		}
		String orderByStr = camelToUnderline(orderBy);
		String directionStr = direction == null ? "desc" : direction.toString().toLowerCase();
		return orderByStr + " " + directionStr;
	}

	private String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append("_");
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
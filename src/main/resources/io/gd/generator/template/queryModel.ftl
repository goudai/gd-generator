package ${meta.queryModelPackage};

import io.gd.generator.api.Direction;
<#if meta.useLombok>
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
</#if>

<#if meta.importFullTypes??>
<#list meta.importFullTypes as importFullType>
import ${importFullType};
</#list>
</#if>

<#if meta.useLombok>
@Getter
@Setter
@Builder
</#if>
public class ${meta.type} {
	<#if meta.queryModelFields??>
	<#list meta.queryModelFields as queryModelField>

	private ${queryModelField.type} ${queryModelField.name};
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
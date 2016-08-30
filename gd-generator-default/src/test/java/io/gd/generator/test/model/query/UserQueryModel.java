package io.gd.generator.test.model.query;

import io.gd.generator.api.query.Direction;

import java.util.HashSet;
import java.util.Set;

public class UserQueryModel {

	private static Set<String> filedNames = new HashSet<>();

	static {
		filedNames.add("registerIp");
		filedNames.add("gender");
		filedNames.add("registerTime");
		filedNames.add("city");
		filedNames.add("sign");
		filedNames.add("avatar");
		filedNames.add("lastLoginIp");
		filedNames.add("lastLoginTime");
		filedNames.add("password");
		filedNames.add("province");
		filedNames.add("phone");
		filedNames.add("district");
		filedNames.add("nickname");
		filedNames.add("id");
		filedNames.add("isTeacher");
		filedNames.add("isFrozen");
		filedNames.add("job");
	}

	private Long idEQ;

	private Long idNEQ;

	private Long[] idIN;

	private String phoneLK;

	private String[] jobIN;

	private Integer pageNumber;

	private Integer pageSize;

	private String orderBy;

	private Direction direction;

	public Long getIdEQ() {
		return idEQ;
	}

	public void setIdEQ(Long idEQ) {
		this.idEQ = idEQ;
	}

	public Long getIdNEQ() {
		return idNEQ;
	}

	public void setIdNEQ(Long idNEQ) {
		this.idNEQ = idNEQ;
	}

	public Long[] getIdIN() {
		return idIN;
	}

	public void setIdIN(Long[] idIN) {
		this.idIN = idIN;
	}

	public String getPhoneLK() {
		return phoneLK;
	}

	public void setPhoneLK(String phoneLK) {
		this.phoneLK = phoneLK;
	}

	public String[] getJobIN() {
		return jobIN;
	}

	public void setJobIN(String[] jobIN) {
		this.jobIN = jobIN;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setOrderBy(String orderBy) {
		if (orderBy == null) {
			this.orderBy = orderBy;
		} else if (!filedNames.contains(orderBy)) {
			throw new IllegalArgumentException("order by is invalid");
		}
	}

	public Long getOffset() {
		if (pageNumber == null || pageSize == null) {
			return null;
		}
		return ((long) pageNumber) * pageSize;
	}

	public String getOrderByAndDirection() {
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
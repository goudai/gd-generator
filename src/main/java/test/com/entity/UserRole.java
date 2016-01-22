package test.com.entity;

import java.io.Serializable;

import javax.persistence.Id;

public class UserRole implements Serializable {
	private static final long serialVersionUID = 7606285422684039887L;
	@Id
	private Long id;
	private Long userId;
	private Long roleId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}

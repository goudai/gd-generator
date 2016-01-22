package test.com.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "admin",uniqueConstraints=@UniqueConstraint(columnNames={"userId,age,age33,age3333"}))
public class Admin implements Serializable {
	private static final long serialVersionUID = 6417007852344218585L;
	@Id
	private Long id;
	private Long userId;
	@Column(unique=true)
	private String roleNames;
	private String roleName33;
	private String roleName334;
	private int age;
	private int age44;
	private int age33;
	private int age3333;

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

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	

}

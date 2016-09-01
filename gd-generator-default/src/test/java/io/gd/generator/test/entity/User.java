package io.gd.generator.test.entity;

import io.gd.generator.annotation.Field;
import io.gd.generator.annotation.query.Query;
import io.gd.generator.annotation.query.QueryModel;
import io.gd.generator.annotation.view.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

import static io.gd.generator.api.query.Predicate.*;


@Entity
@Table(name = "user")
@QueryModel
@ViewObject(groups = { "UserSimpleVo", "UserListVo", "UserDetailVo" },
		collectionViews = {@CollectionView(groups = { "UserDetailVo", "UserSimpleVo" }, name = "blogs",elementGroup = "", type = ArrayList.class)}
		,mapViews = {@MapView(name = "userMaps")}
)

public class User {

	public enum Gender {
		男, 女;
	}

	@Id
	@Query(value = {EQ, NEQ, IN})
	private Long id;

	@Query(value = {LK})
	@Column(length = 11, unique = true)
	@View(name = "phone", type = String.class, groups = { "UserSimpleVo" })
	private String phone;

	@Column(length = 20)
	private String password;

	@Column(length = 6, unique = true)
	private String nickname;

	private Boolean isTeacher;

	@Field(label = "头像")
	private String avatar; // 头像

	private Boolean isFrozen;

	@Temporal(TemporalType.TIMESTAMP)
	private Date registerTime;

	@View(groups = "UserDetailVo", name = "lastLoginTimeLabel", type = String.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTime;

	private String lastLoginIp;

	@MapView(name = "registerMaps")
	private String registerIp;

	@Column(length = 100)
	@AssociationView(associationGroup = "UserSimpleVo")
	private String sign;

	@Enumerated(EnumType.ORDINAL)
	private Gender gender;





	private Long province;

	private Long city;

	private Long district;

	@Query(value = {IN})
	private String job;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Long getProvince() {
		return province;
	}

	public void setProvince(Long province) {
		this.province = province;
	}

	public Long getCity() {
		return city;
	}

	public void setCity(Long city) {
		this.city = city;
	}

	public Long getDistrict() {
		return district;
	}

	public void setDistrict(Long district) {
		this.district = district;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getIsTeacher() {
		return isTeacher;
	}

	public void setIsTeacher(Boolean isTeacher) {
		this.isTeacher = isTeacher;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Boolean getIsFrozen() {
		return isFrozen;
	}

	public void setIsFrozen(Boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}
}
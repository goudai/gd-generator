package io.gd.generator.test.entity;

import io.gd.generator.annotation.Field;
import io.gd.generator.annotation.TypeHandler;
import io.gd.generator.annotation.query.Query;
import io.gd.generator.annotation.query.QueryModel;
import io.gd.generator.annotation.view.*;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.StringTypeHandler;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static io.gd.generator.api.query.Predicate.*;


@Entity
@Table(name = "user")
@QueryModel
@ViewObject(
		groups = {"UserSimpleVo", "UserListVo", "UserDetailVo"},
		views = {@View(name = "user", type = BigDecimal.class, field = @Field(label = "view", order = 1))},
		associationViews = {@AssociationView(name = "test", type = String.class, associationGroup = "UserSimpleVo")},
		collectionViews = {@CollectionView(groups = {"UserDetailVo", "UserSimpleVo"}, name = "blogs", elementGroup = "", type = ArrayList.class)}
		, mapViews = {@MapView(name = "userMaps")
}
)

public class User {

	@Id
	@Query(value = {EQ, NEQ, IN})
	private Long id;
	@Query(value = {LK})
	@Column(length = 11, unique = true)
	@View(name = "phone", type = String.class, groups = {"UserSimpleVo"})

	private String phone;
	@Column(length = 20)
	@AssociationView

	private String password;
	@Column(length = 6, unique = true)
	@MapView(name = "phoneMap")
	private String nickname;
	@CollectionView(name = "collections")
	private Boolean isTeacher;
	@Field(label = "头像")
	private String avatar; // 头像
	private Boolean isFrozen;
	@Temporal(TemporalType.TIMESTAMP)
	private Date registerTime;
	@View(groups = "UserDetailVo", name = "lastLoginTimeLabel", type = String.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Field(label = "最后登录时间")
	private Date lastLoginTime;
	private String lastLoginIp;
	private String registerIp;
	@Column(length = 100)
	@AssociationView(associationGroup = "UserSimpleVo", field = @Field(order = 1))
	private String sign;
	@Query({EQ, NEQ, LK, IN})
	@TypeHandler(EnumTypeHandler.class)
	@Enumerated(EnumType.ORDINAL)
	private Gender gender;
	private Long province;
	private Long city;
	private Long district;
	@Query(value = {IN})
	private String job;
	@Query({EQ, NEQ, LK, IN})
	@Lob
	@TypeHandler(StringTypeHandler.class)
	private Map<String, Object> metadata;

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

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public User setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
		return this;
	}

	public enum Gender {
		男, 女;
	}
}
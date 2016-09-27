### 简介

1. 基于实体类为主导的代码生成器，可基于实体类快速的生成mybatis的相关配置。
如生成接口mapper,生成xml配置文件。
2. 可通过实体类在生成自动建表，自动加索引,自动更新数据列。
3. 可检测出数据库与实体类之间的差异并在日志中打印出对饮警告或者修复用的sql语句。  
如 warn : 数据库中的列  [mysql_name --> mysqlName] 在实体类 Admin 不存在;
4. 支持大部分JPA注解解析，可通过此代码生成器快速从hibernate转换到mybatis。
5. 抽取查询对象，简化查询。QuerModel,自动生成QueryModel
6. 自动驼峰装换
7. 生成VO对象，提供四中注解注册，@View  @AssociationView @CollectionView @MapView

### 安装
        wget https://github.com/goudai/gd-generator/archive/gd-generator-1.0.0.zip
        unzip gd-generator-1.0.0.zip
        cd gd-generator-1.0.0
        mvn clean install 
       
### maven 依赖
```xml
<dependency>
    <groupId>io.goudai</groupId>
    <artifactId>gd-generator-api</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>io.goudai</groupId>
    <artifactId>gd-generator-default</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 注解对照表
#### @ViewObject
* 在实体类上使用,用于标示是否解析该类的VO

属性 | 类型 | 描述
-----|------|----
groups    | String[]    | 需要生成的VO的名称
views    | @Views[]    | 需要生成的基本属性
associationViews    | @AssociationView[]    | 需要生成的对象属性一般填VO的名称
collectionViews    | @CollectionView[]    | 需要生成的集合属性
mapViews    | @MapView[]    | 需要生成的Map属性

#### @View
* 在实体中的属性上使用,标示该属性将被解析为一个VO的属性
一个属性上可以使用多个View注,如果使用View全部默认属性,那么将取得原始字段的name和type

属性 | 类型 | 描述
-----|------|----
name    | String    | 需要生成的字段名成,如果不写将默认取属性名称,注意如果是在ViewObject上使用必须填写
groups    | String[]    | 填写VO的名称,如果不写默认取ViewObject中的属性
type    | Class    | 生成属性的type,一般为String
collectionViews    | @CollectionView[]    | 需要生成的集合属性

#### @AssociationView
* 在实体中的属性上使用,标示该属性将被解析为一个VO的属性
一个属性上可以使用多个AssociationView注

属性 | 类型 | 描述
-----|------|----
name    | String    | 需要生成的字段名成,如果不写将默认取属性名称,注意如果是在ViewObject上使用必须填写
groups    | String[]    | 填写VO的名称,如果不写默认取ViewObject中的属性
type    | Class    | 生成属性的类型,一般为复杂类型 如BigDecimal.class
associationGroup    | String    | 生成属性的类型,一般为VO的名称 注意 与type属性冲突

#### @CollectionView
* 在实体中的属性上使用,标示该属性将被解析为一个VO的属性
一个属性上可以使用多个CollectionView

属性 | 类型 | 描述
-----|------|----
name    | String    | 需要生成的字段名成,如果不写将默认取属性名称,注意如果是在ViewObject上使用必须填写
groups    | String[]    | 填写VO的名称,如果不写默认取ViewObject中的属性
type    | Class    | 需要生成的集合实现类,默认为ArrayList.class
elementType    | Class    | 生成的集合的泛型
elementGroup    | String   | 生成的集合的泛型 与elementType冲突

#### @MapView
* 在实体中的属性上使用,标示该属性将被解析为一个VO的属性
一个属性上可以使用多个MapView

属性 | 类型 | 描述
-----|------|----
name    | String    | 需要生成的字段名成,如果不写将默认取属性名称,注意如果是在ViewObject上使用必须填写
groups    | String[]    | 填写VO的名称,如果不写默认取ViewObject中的属性
type    | Class    | 需要生成的Map实现类,默认为HashMap.class
keyType    | Class    | 生成的Map的Key泛型
keyGroup    | String   | 生成的Map的Key泛型 与keyType冲突
valueType    | Class   | 生成的Map的Value泛型 
valueGroup    | String   | 生成的Map的Key泛型 与valueType冲突




### 快速使用

```java
	public static void main(String[] args) throws Exception {
		Config config = new Config();
		config.setGenLogFile(Paths.get(System.getProperty("user.home") , "yourProject.log").toString());
		config.setUrl("jdbc:mysql://mysqlIP/yourdb");
		config.setEntityPackage("com.xx.entity");
		config.setMybatisMapperPackage("com.xx.mapper");
		
		//D:\\Work\\Workspace\\xx-parent
		//Users/freeman/IdeaProjects/xxx-parent
		String projectPath = "your project base path";

        //xxx-service-impl\\src\\main\\java\\com\\xx\\mapper & windows
        //xxx-service-impl/src/main/java/com/xx/mapper & max or linux
		config.setMybatisMapperPath(projectPath + "your mybatis mapper path");
		
		//\\xx-service-impl\\src\\main\\resources\\com\\xx\\mapping & windows
		//\\xx-service-impl/src/main/resources/com/xx/mapping & max or linux
		config.setMybatisXmlPath(projectPath + "your mybatis mapping xml path");
		config.setUsername("your db user");
		config.setPassword("your db password");
        
        // is use lombok default : true
		config.setUseLombok(true);
         //com.xx.model.query
		config.setQueryModelPackage("your query model package name");
		//\\xx-service\\src\\main\\java\\com\\xx\\model\\query & windows
		//\\xx-service/src/main/java/com/xx/model/query & max or linux
		config.setQueryModelPath(projectPath + "your query model package name");

		Generator.generate(config,
				new VoHandler("com.xx.vo", projectPath + "\\xx-component\\src\\main\\java\\com\\xx\\vo", true),
				new QueryModelHandler(),
				new MybatisMapperHandler(),
				new MybatisXmlHandler(),
				new MysqlHandler()
		);
	}
```


```java
/* JPA注解 需要解析的实体类必须加此注解*/
@Entity
@Table(name = "usr_user")
/*lombok提供注解*/
@Getter
@Setter
/*此注解表示是否生成查询对象*/
@QueryModel
/*此注解为预留注解 为以后生成页面以及生成excel*/
@Type(label = "用户")
@ViewObject(groups = {VO_LIST, VO_SIMPLE, VO_ADMIN, VO_ADMIN_SIMPLE, VO_ADMIN_FULL},
	collectionViews = {
		@CollectionView(name = "userUpgrades", elementGroup = UserUpgrade.VO_ADMIN, groups = VO_ADMIN_FULL),
		@CollectionView(name = "teammates", elementGroup = VO_ADMIN_SIMPLE, groups = VO_ADMIN_FULL)
	},
	associationViews = {
		@AssociationView(name = "portrait", associationGroup = Portrait.VO_ADMIN, groups = VO_ADMIN_FULL),
		@AssociationView(name = "appearance", associationGroup = Appearance.VO_ADMIN, groups = VO_ADMIN_FULL)
	}

)
public class User implements Serializable {
      /*此处定义的为需要生成的VO的类名称*/
	public static final String VO_LIST = "UserListVo";
	public static final String VO_SIMPLE = "UserSimpleVo";
	public static final String VO_ADMIN = "UserAdminVo";
	public static final String VO_ADMIN_SIMPLE = "UserAdminSimpleVo";
	public static final String VO_ADMIN_FULL = "UserAdminFullVo";

	@Id
	@Query(Predicate.IN)
	@Field(label = "id")
	@View
	private Long id;

	@Column(length = 11, unique = true)
	@NotBlank
	@Pattern(regexp = "^1[\\d]{10}$")
	@StringBinder
	@Query(Predicate.EQ)
	@Field(label = "手机号")
	@View(groups = {VO_LIST, VO_ADMIN, VO_ADMIN_SIMPLE, VO_ADMIN_FULL})
	private String phone;

	@Column(length = 60)
	@Field(label = "密码")
	private String password;

	@Column(length = 60)
	@NotBlank
	@StringBinder
	@Query(Predicate.LK)
	@Length(max = 60)
	@Field(label = "昵称")
	@View
	private String nickname;

	@NotNull
	@Query(Predicate.EQ)
	@Field(label = "用户类型")
	@View(groups = {VO_ADMIN, VO_ADMIN_SIMPLE, VO_ADMIN_FULL})
	private UserType userType;

	@NotNull
	@Query(Predicate.EQ)
	@Field(label = "用户等级")
	@View(groups = {VO_LIST, VO_ADMIN, VO_ADMIN_SIMPLE, VO_ADMIN_FULL})
	@View(name = "userRankLabel", type = String.class, groups = {VO_ADMIN, VO_ADMIN_SIMPLE, VO_ADMIN_FULL})
	private UserRank userRank;

	@Column(length = 11)
	@Pattern(regexp = "^[\\d]{5,11}$")
	@Field(label = "qq")
	@View(groups = {VO_ADMIN, VO_ADMIN_FULL})
	private String qq;

	@NotBlank
	@Field(label = "头像")
	@View(name = "avatarThumbnail", type = String.class)
	@Length(max = 250)
	private String avatar;

	@NotNull
	@Query(Predicate.EQ)
	@Field(label = "是否冻结")
	@View(groups = {VO_ADMIN, VO_ADMIN_FULL})
	private Boolean isFrozen;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Query({Predicate.GTE, Predicate.LT})
	@Field(label = "注册时间")
	@View(groups = {VO_ADMIN, VO_ADMIN_FULL})
	private Date registerTime;

	@NotNull
	@Field(label = "注册ip")
	@View(groups = {VO_ADMIN, VO_ADMIN_FULL})
	private String registerIp;

	@Query({Predicate.EQ, Predicate.IN})
	@Field(label = "邀请人id", description = "此用户不是最终上下级关系")
	@AssociationView(name = "inviter", associationGroup = VO_ADMIN_SIMPLE, groups = {VO_ADMIN, VO_ADMIN_FULL})
	private Long inviterId;

	@Query({Predicate.EQ, Predicate.IN})
	@Field(label = "上级id")
	@AssociationView(name = "parent", associationGroup = VO_ADMIN_SIMPLE, groups = {VO_ADMIN, VO_ADMIN_FULL})
	private Long parentId;

	@Field(label = "remark")
	@View(groups = {VO_ADMIN, VO_ADMIN_FULL})
	private String remark;

	@Temporal(TemporalType.DATE)
	@Field(label = "vipExpiredDate")
	private Date vipExpiredDate;

	@Column(length = 60, unique = true)
	@Field(label = "微信openId")
	private String openId;

	@Column(length = 60, unique = true)
	@Field(label = "微信unionId")
	private String unionId;

	@Field(label = "上次升级时间")
	private Date lastUpgradedTime;

}
```

### 生成的结果   

#### 实体类Mapper
```java
public interface UserMapper {

	int insert(User user);

	int update(User user);

	int merge(@Param("user") User user, @Param("fields")String... fields);

	int delete(Long id);

	User findOne(Long id);

	List<User> findAll(UserQueryModel userQueryModel);

	long count(UserQueryModel userQueryModel);

	User findByPhone(String phone);

	User findByOpenId(String openId);

}
```        

       
#### Mybatis XML
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.zy.mapper.UserMapper" >

	<resultMap id="baseResultMap" type="com.zy.entity.usr.User">
		<id column="id" property="id" />
		<result column="phone" property="phone" />
		<result column="password" property="password" />
		<result column="nickname" property="nickname" />
		<result column="user_type" property="userType" typeHandler="org.apache.ibatis.type.EnumOrdinalTypeHandler" />
		<result column="user_rank" property="userRank" typeHandler="org.apache.ibatis.type.EnumOrdinalTypeHandler" />
		<result column="qq" property="qq" />
		<result column="avatar" property="avatar" />
		<result column="is_frozen" property="isFrozen" />
		<result column="register_time" property="registerTime" />
		<result column="register_ip" property="registerIp" />
		<result column="inviter_id" property="inviterId" />
		<result column="parent_id" property="parentId" />
		<result column="remark" property="remark" />
		<result column="vip_expired_date" property="vipExpiredDate" />
		<result column="open_id" property="openId" />
		<result column="union_id" property="unionId" />
		<result column="last_upgraded_time" property="lastUpgradedTime" />
	</resultMap>

	<insert id="insert" parameterType="com.zy.entity.usr.User" useGeneratedKeys="true" keyProperty="id">
	  insert into `usr_user` (phone,password,nickname,user_type,user_rank,qq,avatar,is_frozen,register_time,register_ip,inviter_id,parent_id,remark,vip_expired_date,open_id,union_id,last_upgraded_time)
	  values (#{phone},#{password},#{nickname},#{userType,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler},#{userRank,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler},#{qq},#{avatar},#{isFrozen},#{registerTime},#{registerIp},#{inviterId},#{parentId},#{remark},#{vipExpiredDate},#{openId},#{unionId},#{lastUpgradedTime})
	</insert>

	<delete id="delete">
		delete from `usr_user` where id = #{id}
	</delete>

	<update id="update" parameterType="com.zy.entity.usr.User">
		update `usr_user`
		<set>
			phone = #{phone},
			password = #{password},
			nickname = #{nickname},
			user_type = #{userType,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler},
			user_rank = #{userRank,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler},
			qq = #{qq},
			avatar = #{avatar},
			is_frozen = #{isFrozen},
			register_time = #{registerTime},
			register_ip = #{registerIp},
			inviter_id = #{inviterId},
			parent_id = #{parentId},
			remark = #{remark},
			vip_expired_date = #{vipExpiredDate},
			open_id = #{openId},
			union_id = #{unionId},
			last_upgraded_time = #{lastUpgradedTime},
		</set>
		where id = #{id}
	</update>

	<update id="merge">
		update `usr_user`
			<set>
				<foreach collection="fields" item="field">
				<choose>
					<when test="field == 'phone'">phone = #{user.phone},</when>
					<when test="field == 'password'">password = #{user.password},</when>
					<when test="field == 'nickname'">nickname = #{user.nickname},</when>
					<when test="field == 'userType'">user_type = #{user.userType,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler,javaType=com.zy.entity.usr.User$UserType},</when>
					<when test="field == 'userRank'">user_rank = #{user.userRank,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler,javaType=com.zy.entity.usr.User$UserRank},</when>
					<when test="field == 'qq'">qq = #{user.qq},</when>
					<when test="field == 'avatar'">avatar = #{user.avatar},</when>
					<when test="field == 'isFrozen'">is_frozen = #{user.isFrozen},</when>
					<when test="field == 'registerTime'">register_time = #{user.registerTime},</when>
					<when test="field == 'registerIp'">register_ip = #{user.registerIp},</when>
					<when test="field == 'inviterId'">inviter_id = #{user.inviterId},</when>
					<when test="field == 'parentId'">parent_id = #{user.parentId},</when>
					<when test="field == 'remark'">remark = #{user.remark},</when>
					<when test="field == 'vipExpiredDate'">vip_expired_date = #{user.vipExpiredDate},</when>
					<when test="field == 'openId'">open_id = #{user.openId},</when>
					<when test="field == 'unionId'">union_id = #{user.unionId},</when>
					<when test="field == 'lastUpgradedTime'">last_upgraded_time = #{user.lastUpgradedTime},</when>
				</choose>
				</foreach>
			</set>
		where id = #{user.id}
	</update>

	<select id="findOne" resultMap="baseResultMap" parameterType="long">
		select
		id,phone,password,nickname,user_type,user_rank,qq,avatar,is_frozen,register_time,register_ip,inviter_id,parent_id,remark,vip_expired_date,open_id,union_id,last_upgraded_time
		from `usr_user`
		where id = #{id}
	</select>

	<select id="findAll" resultMap="baseResultMap" parameterType="com.zy.model.query.UserQueryModel">
		select
		id,phone,password,nickname,user_type,user_rank,qq,avatar,is_frozen,register_time,register_ip,inviter_id,parent_id,remark,vip_expired_date,open_id,union_id,last_upgraded_time
		from `usr_user`
		<where>
			<if test="userRankEQ != null">
				and user_rank = #{userRankEQ,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}
			</if>
			<if test="nicknameLK != null">
				<bind name="nicknameLK" value="'%' + nicknameLK + '%'"/> and nickname like #{nicknameLK}
			</if>
			<if test="inviterIdEQ != null">
				and inviter_id = #{inviterIdEQ}
			</if>
			<if test="parentIdIN != null">
				<if test="parentIdIN.length != 0">
				and parent_id in
				<foreach collection="parentIdIN" item="item" open="(" separator="," close=")">
				#{item}
				</foreach>
				</if>
				<if test="parentIdIN.length == 0">
				1 = 2
				</if>
			</if>
			<if test="isFrozenEQ != null">
				and is_frozen = #{isFrozenEQ}
			</if>
			<if test="idIN != null">
				<if test="idIN.length != 0">
				and id in
				<foreach collection="idIN" item="item" open="(" separator="," close=")">
				#{item}
				</foreach>
				</if>
				<if test="idIN.length == 0">
				1 = 2
				</if>
			</if>
			<if test="userTypeEQ != null">
				and user_type = #{userTypeEQ,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}
			</if>
			<if test="registerTimeGTE != null">
				and register_time &gt;= #{registerTimeGTE}
			</if>
			<if test="registerTimeLT != null">
				and register_time &lt; #{registerTimeLT}
			</if>
			<if test="parentIdEQ != null">
				and parent_id = #{parentIdEQ}
			</if>
			<if test="phoneEQ != null">
				and phone = #{phoneEQ}
			</if>
			<if test="inviterIdIN != null">
				<if test="inviterIdIN.length != 0">
				and inviter_id in
				<foreach collection="inviterIdIN" item="item" open="(" separator="," close=")">
				#{item}
				</foreach>
				</if>
				<if test="inviterIdIN.length == 0">
				1 = 2
				</if>
			</if>
		</where>
		<choose>
			<when test="orderByAndDirection != null">
				order by ${orderByAndDirection}
			</when>
			<otherwise>
				order by id desc
			</otherwise>
		</choose>
		<if test="offset != null">
			limit #{offset}, #{pageSize}
		</if>
	</select>

	<select id="count" resultType="_long" parameterType="com.zy.model.query.UserQueryModel">
		select count(*) from `usr_user`
		<where>
			<if test="userRankEQ != null">
				and user_rank = #{userRankEQ,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}
			</if>
			<if test="nicknameLK != null">
				<bind name="nicknameLK" value="'%' + nicknameLK + '%'"/> and nickname like #{nicknameLK}
			</if>
			<if test="inviterIdEQ != null">
				and inviter_id = #{inviterIdEQ}
			</if>
			<if test="parentIdIN != null">
				<if test="parentIdIN.length != 0">
				and parent_id in
				<foreach collection="parentIdIN" item="item" open="(" separator="," close=")">
				#{item}
				</foreach>
				</if>
				<if test="parentIdIN.length == 0">
				1 = 2
				</if>
			</if>
			<if test="isFrozenEQ != null">
				and is_frozen = #{isFrozenEQ}
			</if>
			<if test="idIN != null">
				<if test="idIN.length != 0">
				and id in
				<foreach collection="idIN" item="item" open="(" separator="," close=")">
				#{item}
				</foreach>
				</if>
				<if test="idIN.length == 0">
				1 = 2
				</if>
			</if>
			<if test="userTypeEQ != null">
				and user_type = #{userTypeEQ,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}
			</if>
			<if test="registerTimeGTE != null">
				and register_time &gt;= #{registerTimeGTE}
			</if>
			<if test="registerTimeLT != null">
				and register_time &lt; #{registerTimeLT}
			</if>
			<if test="parentIdEQ != null">
				and parent_id = #{parentIdEQ}
			</if>
			<if test="phoneEQ != null">
				and phone = #{phoneEQ}
			</if>
			<if test="inviterIdIN != null">
				<if test="inviterIdIN.length != 0">
				and inviter_id in
				<foreach collection="inviterIdIN" item="item" open="(" separator="," close=")">
				#{item}
				</foreach>
				</if>
				<if test="inviterIdIN.length == 0">
				1 = 2
				</if>
			</if>
		</where>
	</select>

	<select id="findByPhone" resultMap="baseResultMap">
		select * from `usr_user` where phone = #{phone}
	</select>

	<select id="findByOpenId" resultMap="baseResultMap">
		select * from `usr_user` where open_id = #{openId}
	</select>

</mapper>        
```	 
   
####UserQueryModel   
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryModel implements Serializable {

	private Long[] idIN;

	private String phoneEQ;

	private String nicknameLK;

	private UserType userTypeEQ;

	private UserRank userRankEQ;

	private Boolean isFrozenEQ;

	private Date registerTimeGTE;

	private Date registerTimeLT;

	private Long inviterIdEQ;

	private Long[] inviterIdIN;

	private Long parentIdEQ;

	private Long[] parentIdIN;

	private Integer pageNumber;

	private Integer pageSize;

	private String orderBy;

	private Direction direction;

	public void setOrderBy(String orderBy) {
		if (orderBy != null && !fieldNames.contains(orderBy)) {
			throw new IllegalArgumentException("order by is invalid");
		}
		this.orderBy = orderBy;
	}

	public Long getOffset() {
		if (pageNumber == null || pageSize == null) {
			return null;
		}
		return ((long) pageNumber) * pageSize;
	}

	public String getOrderByAndDirection() {
		if (orderBy == null) {
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

	private static Set<String> fieldNames = new HashSet<>();

	static {
		fieldNames.add("qq");
		fieldNames.add("registerIp");
		fieldNames.add("unionId");
		fieldNames.add("registerTime");
		fieldNames.add("openId");
		fieldNames.add("remark");
		fieldNames.add("lastUpgradedTime");
		fieldNames.add("userRank");
		fieldNames.add("avatar");
		fieldNames.add("parentId");
		fieldNames.add("password");
		fieldNames.add("phone");
		fieldNames.add("inviterId");
		fieldNames.add("nickname");
		fieldNames.add("vipExpiredDate");
		fieldNames.add("id");
		fieldNames.add("userType");
		fieldNames.add("isFrozen");
	}

}
```
###VO
####UserListVo
```java
@Getter
@Setter
public class UserListVo implements Serializable {
	/* 原生 */
	@Field(label = "id")
	private Long id;
	@Field(label = "手机号")
	private String phone;
	@Field(label = "昵称")
	private String nickname;
	@Field(label = "用户等级")
	private UserRank userRank;

	/* 扩展 */
	@Field(label = "头像")
	private String avatarThumbnail;

}
```
####UserSimpleVo
```java
@Getter
@Setter
public class UserSimpleVo implements Serializable {
	/* 原生 */
	@Field(label = "id")
	private Long id;
	@Field(label = "昵称")
	private String nickname;

	/* 扩展 */
	@Field(label = "头像")
	private String avatarThumbnail;

}
```
####UserAdminVo
```java
@Getter
@Setter
public class UserAdminVo implements Serializable {
	/* 原生 */
	@Field(label = "id")
	private Long id;
	@Field(label = "手机号")
	private String phone;
	@Field(label = "昵称")
	private String nickname;
	@Field(label = "用户类型")
	private UserType userType;
	@Field(label = "用户等级")
	private UserRank userRank;
	@Field(label = "qq")
	private String qq;
	@Field(label = "是否冻结")
	private Boolean isFrozen;
	@Field(label = "注册时间")
	private Date registerTime;
	@Field(label = "注册ip")
	private String registerIp;
	@Field(label = "remark")
	private String remark;

	/* 扩展 */
	@Field(label = "用户等级")
	private String userRankLabel;
	@Field(label = "头像")
	private String avatarThumbnail;
	@Field(label = "邀请人id")
	private UserAdminSimpleVo inviter;
	@Field(label = "上级id")
	private UserAdminSimpleVo parent;

}
```
####UserAdminSimpleVo
```java
@Getter
@Setter
public class UserAdminSimpleVo implements Serializable {
	/* 原生 */
	@Field(label = "id")
	private Long id;
	@Field(label = "手机号")
	private String phone;
	@Field(label = "昵称")
	private String nickname;
	@Field(label = "用户类型")
	private UserType userType;
	@Field(label = "用户等级")
	private UserRank userRank;

	/* 扩展 */
	@Field(label = "用户等级")
	private String userRankLabel;
	@Field(label = "头像")
	private String avatarThumbnail;

}
```
####UserAdminFullVo
```java
@Getter
@Setter
public class UserAdminFullVo implements Serializable {
	/* 原生 */
	@Field(label = "id")
	private Long id;
	@Field(label = "手机号")
	private String phone;
	@Field(label = "昵称")
	private String nickname;
	@Field(label = "用户类型")
	private UserType userType;
	@Field(label = "用户等级")
	private UserRank userRank;
	@Field(label = "qq")
	private String qq;
	@Field(label = "是否冻结")
	private Boolean isFrozen;
	@Field(label = "注册时间")
	private Date registerTime;
	@Field(label = "注册ip")
	private String registerIp;
	@Field(label = "remark")
	private String remark;

	/* 扩展 */
	@Field(label = "portrait")
	private PortraitAdminVo portrait;
	@Field(label = "appearance")
	private AppearanceAdminVo appearance;
	@Field(label = "用户等级")
	private String userRankLabel;
	@Field(label = "头像")
	private String avatarThumbnail;
	@Field(label = "邀请人id")
	private UserAdminSimpleVo inviter;
	@Field(label = "上级id")
	private UserAdminSimpleVo parent;
	@Field(label = "userUpgrades")
	private List<UserUpgradeAdminVo> userUpgrades = new ArrayList<>();
	@Field(label = "teammates")
	private List<UserAdminSimpleVo> teammates = new ArrayList<>();

}
```
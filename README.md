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


### 注解对照表
#### @ViewObject
* 在实体类上使用,用户标示是否解析该类的VO
|:--------------:|:--——------------|:------------------:|
|groups          |views[]          | 需要生成的VO的昵称    |
|views           |views[]          | 需要生成的基本属性    |
|associationViews|AssociationView[]| 需要生成的对象属性    |
|collectionViews |views[]          | 需要生成的集合属性    |
|mapViews        |MapView[]        | 需要生成的map属性    |


### 实体类demo
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
### 生成的结果   

#### 实体类Mapper
```java
public interface AdminMapper {
    
	int insert(Admin admin);
	    
	int update(Admin admin);
	    
	int merge(@Param("admin") Admin admin, @Param("fields")String... fields);
	    
	int delete(Long id);
	    
	Admin findOne(Long id);
	    
	List<Admin> findAll(AdminQueryModel adminQueryModel);
	    
	long count(AdminQueryModel adminQueryModel);
}
```        
#### AdminQueryModel
```java
public class AdminQueryModel  {
    private static final long serialVersionUID = 1L;
    private String nicknameLK;
    private String phoneEQ;
    
    public String getNicknameLK() {
        return nicknameLK;
    }
    
    public void setNicknameLK(String nicknameLK) {
        this.nicknameLK = nicknameLK;
    }
    
    public String getPhoneEQ() {
        return phoneEQ;
    }
    
    public void setPhoneEQ(String phoneEQ) {
        this.phoneEQ = phoneEQ;
    }
    
}
```        
#### Mybatis XML
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="test.com.mapper.AdminMapper" >
    
<resultMap id="baseResultMap" type="test.com.entity.Admin">
    <id column="id" property="id" />
    <result column="user_id" property="userId" />
    <result column="role_names" property="roleNames" />
    <result column="age" property="age" />
</resultMap>
    
<insert id="insert" parameterType="test.com.entity.Admin" useGeneratedKeys="true" keyProperty="id">
  insert into `admin` (user_id,role_names,age)
  values (#{userId},#{roleNames},#{age})
</insert>
    
<delete id="delete">
    delete from `admin` where id = #{id}
</delete>
    
<update id="update" parameterType="test.com.entity.Admin">
    update `admin`
    <set>
        user_id = #{userId},
        role_names = #{roleNames},
        age = #{age},
    </set>
    where id = #{id}
</update>
    
<update id="merge">
    update `admin`
        <set>
            <foreach collection="fields" item="field">
            <choose>
                <when test="field == 'userId'">user_id = #{admin.userId},</when>
                <when test="field == 'roleNames'">role_names = #{admin.roleNames},</when>
                <when test="field == 'age'">age = #{admin.age},</when>
            </choose>
            </foreach>
        </set>
    where id = #{admin.id}
</update>
    
<select id="findOne" resultMap="baseResultMap" parameterType="long">
    select
    id,user_id,role_names,age
    from `admin`
    where id = #{id}
</select>
    
<select id="findAll" resultMap="baseResultMap" parameterType="test.com.model.query.AdminQueryModel">
    select
    id,user_id,role_names,age
    from `admin`
    <where>
        <if test="nicknameLK != null">
            <bind name="nicknameLK" value="'%' + nicknameLK + '%'"/> and nickname like #{nicknameLK}
        </if>
        <if test="phoneEQ != null">
             and phone = #{phoneEQ}
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
    
<select id="count" resultType="_long" parameterType="test.com.model.query.AdminQueryModel">
    select count(*) from `admin`
    <where>
        <if test="nicknameLK != null">
            <bind name="nicknameLK" value="'%' + nicknameLK + '%'"/> and nickname like #{nicknameLK}
        </if>
        <if test="phoneEQ != null">
             and phone = #{phoneEQ}
        </if>
    </where>
</select>
	    
</mapper>         
```	    

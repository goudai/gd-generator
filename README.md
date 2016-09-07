### 简介

1. 基于实体类为主导的代码生成器，可基于实体类快速的生成mybatis的相关配置。
如生成接口mapper,生成xml配置文件。
2. 可通过实体类在生成自动建表，自动加索引,自动更新数据列。
3. 可检测出数据库与实体类之间的差异并在日志中打印出对饮警告或者修复用的sql语句。  
如 warn : 数据库中的列  [mysql_name --> mysqlNname] 在实体类 Admin 不存在;
4. 支持大部分JPA注解解析，可通过此代码生成器快速从hibernate转换到mybatis。
5. 抽取查询对象，简化查询。QuerModel
6. 自动驼峰装换
update ========
1. 生成查询对象，提供查询相关注解
2. 生成VO对象，提供四中注解注册，@View  @AssociationView @CollectionView @MapView
### 实体类demo
```java
//JPA注解 需要解析的类必须加此注解
@Entity
//JPA注解 name表示数据表的名称 uniqueConstraints表示需要进行唯一约束的列,可自动追加到数据库
@Table(name = "admin",uniqueConstraints=@UniqueConstraint(columnNames={"userId,age"}))
public class Admin implements Serializable {
private static final long serialVersionUID = 1L;
    //JPA 注解 此注解会自动同步数据为主键
    @Id
    private Long id;
    private Long userId;
    //唯一约束
    @Column(unique=true)//
    private String roleNames;
    private int age;
    
    //getter setter
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

        //xxx-service-impl\\src\\main\\java\\com\\zy\\mapper & windows
        //xxx-service-impl/src/main/java/com/zy/mapper & max or linux
		config.setMybatisMapperPath(projectPath + "your mybatis mapper path");
		
		//\\xx-service-impl\\src\\main\\resources\\com\\zy\\mapping & windows
		//\\xx-service-impl/src/main/resources/com/zy/mapping & max or linux
		config.setMybatisXmlPath(projectPath + "your mybatis mapping xml path");
		config.setUsername("your db user");
		config.setPassword("your db password");
        
        // is use lombok default : true
		config.setUseLombok(true);
         //com.xx.model.query
		config.setQueryModelPackage("your query model package name");
		//\\zy-service\\src\\main\\java\\com\\xx\\model\\query & windows
		//\\zy-service/src/main/java/com/xx/model/query & max or linux
		config.setQueryModelPath(projectPath + "your query model package name");

		Generator.generate(config,
				new VoHandler("com.zy.vo", projectPath + "\\zy-component\\src\\main\\java\\com\\zy\\vo", true),
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

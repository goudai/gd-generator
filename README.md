### 简介

1. 基于实体类为主导的代码生成器，可基于实体类快速的生成mybatis的相关配置。
如生成接口mapper,生成xml配置文件。
2. 可通过实体类在生成自动建表，自动加索引,自动更新数据列。
3. 可检测出数据库与实体类之间的差异并在日志中打印出对饮警告或者修复用的sql语句。  
如 warn : 数据库中的列  [mysql_ame --> mysqlNname] 在实体类 Admin 不存在;
4. 支持大部分JPA注解解析，可通过此代码生成器快速从hibernate转换到mybatis。
5. 抽取查询对象，简化查询。QuerModel
6. 自动驼峰装换

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
### 查询对象DEMO
```java
//注意查询对象是由于Admin+QueryModel来进行命名
public class AdminQueryModel  {
    private static final long serialVersionUID = -8493398486786898485L;
    //字段命名规则为需要进行的操作的属性名+操作符号 操作符会在代码解析的时候进行对应解析
    //支持几乎大部分的操作符解析 集体解析请看src\main\java\com\code\core\parse\MybatisXmlParser.java
    private String nicknameLK;
    private String phoneEQ;
    
    //getter setter
}
```
### 快速使用

```java
public static void main(String[] S) {
        Config config = new Config();
        config.setGenLogFile(System.getProperty("user.home") + File.pathSeparator + "/gd-test.log");
        config.setUrl("jdbc:mysql://192.168.10.240/sk");
        config.setEntityPackage("com.sk.entity");
        config.setMybatisMapperPackage("com.sk.mapper");
        config.setQueryModelPackage("com.sk.model.query");
        config.setMybatisXmlPackage("com.sk.mapping");
        config.setJavaSrc("/Users/freeman/IdeaProjects/miziProjects/sk/sk-service/src/main/java");
        config.setResources("/Users/freeman/IdeaProjects/miziProjects/sk/sk-service-impl/src/main/resources");
        config.setUsername("root");
        config.setPassword("123456");
        AbstractGenerator generator = new MybatisGenerator(config);
        //		NodeConfig nodeConfig = new NodeConfig();
        //		nodeConfig.setDistFile(new File("./dubbo.js"));
        //		nodeConfig.setDocFile(new File("./doc.js"));
        //		nodeConfig.setServicePackage("com.sk.service");
        //		generator = new NodeGenerator(nodeConfig);
        generator.generate();
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

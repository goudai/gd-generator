package io.gd.generator.handler;

import io.gd.generator.annotation.TypeHandler;
import io.gd.generator.annotation.query.Query;
import io.gd.generator.annotation.query.QueryModel;
import io.gd.generator.api.query.Predicate;
import io.gd.generator.meta.mybatis.MybatisXmlMeta;
import io.gd.generator.meta.mybatis.MybatisXmlMeta.MybatisMappingMeta;
import io.gd.generator.util.ClassHelper;
import io.gd.generator.util.ConfigChecker;
import io.gd.generator.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.persistence.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisXmlHandler extends ScopedHandler<MybatisXmlMeta> {

    private boolean useGeneratedKeys = true;

    public MybatisXmlHandler() {
        this.useGeneratedKeys = true;
    }

    public MybatisXmlHandler(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    @Override
    protected void init() throws Exception {
        super.init();
        ConfigChecker.notBlank(config.getMybatisXmlPath(), "config mybatisXmlPath is miss");

        String xmlPath = config.getMybatisXmlPath();

        /* 初始化文件夹 */
        File xmlPathDir = new File(xmlPath);
        if (!xmlPathDir.exists()) {
            xmlPathDir.mkdirs();
        } else if (!xmlPathDir.isDirectory()) {
            throw new IllegalArgumentException("xmlPath is not a directory");
        }

    }

    private String getXmlFilePath(Class<?> entityClass) {
        return config.getMybatisXmlPath() + File.separator + entityClass.getSimpleName() + "Mapper.xml";
    }

    @Override
    protected void preRead(Class<?> entityClass) throws Exception {

    }

    @Override
    protected MybatisXmlMeta read(Class<?> entityClass) throws Exception {
        MybatisXmlMeta meta = new MybatisXmlMeta();
        File file = new File(getXmlFilePath(entityClass));
        if (file.exists()) {
            SAXReader reader = new SAXReader();
            reader.setEntityResolver(new MyEntityResolver());
            Document doc = reader.read(file);
            if (doc != null) {
                @SuppressWarnings("unchecked")
                List<Element> elements = doc.getRootElement().elements();
                elements.forEach((element) -> {
                    String id = element.attribute("id").getStringValue().intern();
                    final boolean isAdd =
                            id == "delete".intern()
                                    || id == "insert".intern()
                                    || id == "update".intern()
                                    || id == "findOne".intern()
                                    || id == "findAll".intern()
                                    || id == "baseResultMap".intern()
                                    || id == "merge".intern()
                                    || id == "count".intern()
                                    || id == "baseColumn".intern()
                                    || id == "condition".intern();
                    if (!isAdd) {
                        meta.getOtherMappings().add(element.asXML());
                    }
                });
            }
        }
        return meta;
    }

    @Override
    protected MybatisXmlMeta parse(Class<?> entityClass) throws Exception {
        String mapperName = config.getMybatisMapperPackage() + "." + entityClass.getSimpleName() + "Mapper";
        MybatisXmlMeta meta = new MybatisXmlMeta();
        meta.setMapperName(mapperName);
        meta.setModel(entityClass.getName());
        @SuppressWarnings("unused")
        String tableName = ClassHelper.resolveTableName(entityClass);
        //meta.setTable(StringUtils.camelToUnderline(entityClass.getSimpleName()).replaceFirst("\\_", " ")); bug fix

        meta.setTable(ClassHelper.resolveTableName(entityClass));

        meta.setSimpleName(entityClass.getSimpleName());
        parseBasic(entityClass, meta);
        QueryModel queryModel = entityClass.getAnnotation(QueryModel.class);

        if (queryModel != null) {
            meta.setHasQueryModel(true);
            meta.setQuery(config.getQueryModelPackage() + "." + entityClass.getSimpleName() + config.getQueryModelSuffix());
            ClassHelper.getFields(entityClass).stream().filter(ClassHelper::isNotStaticField).forEach(v -> {
                parseQueryModel(meta, v);
            });
        }
        return meta;
    }

    @Override
    protected MybatisXmlMeta merge(MybatisXmlMeta parsed, MybatisXmlMeta read, Class<?> entityClass) throws Exception {
        if (read != null) {
            parsed.setOtherMappings(read.getOtherMappings());
        }
        return parsed;
    }

    @Override
    protected void write(MybatisXmlMeta merged, Class<?> entityClass) throws Exception {
        Map<String, Object> model = new HashMap<>();
        merged.setUseGeneratedKeys(this.useGeneratedKeys);
        model.put("meta", merged);
        String xml = renderTemplate("mybatisXml", model);
        File file = new File(getXmlFilePath(entityClass));

        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(xml.getBytes());
        }

    }

    @Override
    protected void postWrite(Class<?> entityClass) throws Exception {
        // DO NOTING
    }

    /**
     * <if test="registerTimeLT != null"> and register_time &lt;
     * #{registerTimeLT} </if> nicknameLK = key
     * <p>
     * <if test="nicknameLK != null"> <bind name="nicknameLK"
     * value="'%' + _parameter.getNicknameLK() + '%'"/> and nickname like
     * #{nicknameLK} </if>
     *
     * @param meta
     * @param field
     */
    private void parseQueryModel(MybatisXmlMeta meta, Field field) {
        String name = field.getName();
        Column column = field.getDeclaredAnnotation(Column.class);
        String camelToUnderlineName = StringUtils.camelToUnderline(name);
        if (column != null) {
            if (StringUtils.isNotBlank(column.name())) {
                camelToUnderlineName = column.name();
            }
        }
        camelToUnderlineName = StringUtils.isNotBlank(camelToUnderlineName) ? camelToUnderlineName : "id";
        if (config.isEscapeColumn()) {
            camelToUnderlineName = "`" + camelToUnderlineName + "`";
        }
        if (meta.getIdColumnName() == null) {
            Id idAnno = field.getDeclaredAnnotation(Id.class);
            if (idAnno != null) {
                meta.setIdColumnName(camelToUnderlineName);
                meta.setIdPropName(name);
            }
        }

        camelToUnderlineName = "`" + meta.getTable() + "`." + camelToUnderlineName;
        Query query = field.getAnnotation(Query.class);
        String value = null;
        Predicate[] predicates;
        if (query != null && (predicates = query.value()) != null) {
            for (Predicate predicate : predicates) {
                String typeHandlerClass = resolveTypeHandler(field);
                String nameWithPredicate = name + predicate.toString();
                String bind;
                switch (predicate) {
                    case EQ:
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = "and " + camelToUnderlineName + " = #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = "and " + camelToUnderlineName + " = #{" + nameWithPredicate + "}";
                        break;
                    case NEQ:
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = "and " + camelToUnderlineName + " != #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = "and " + camelToUnderlineName + " != #{" + nameWithPredicate + "}";
                        break;
                    case GT:
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = "and " + camelToUnderlineName + " &gt; #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = "and " + camelToUnderlineName + " &gt; #{" + nameWithPredicate + "}";
                        break;
                    case GTE:
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = "and " + camelToUnderlineName + " &gt;= #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = "and " + camelToUnderlineName + " &gt;= #{" + nameWithPredicate + "}";
                        break;
                    case LT:
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = "and " + camelToUnderlineName + " &lt; #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = "and " + camelToUnderlineName + " &lt; #{" + nameWithPredicate + "}";
                        break;
                    case LTE:
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = "and " + camelToUnderlineName + " &lt;= #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = "and " + camelToUnderlineName + " &lt;= #{" + nameWithPredicate + "}";
                        break;
                    case EW:
                        bind = "<bind name=\"" + nameWithPredicate + "\" value=\"'%' + " + nameWithPredicate + "\"/>";
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + "}";
                        break;
                    case SW:
                        bind = "<bind name=\"" + nameWithPredicate + "\" value=\"" + nameWithPredicate + " + '%'\"/>";
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + "}";
                        break;
                    case LK:
                        bind = "<bind name=\"" + nameWithPredicate + "\" value=\"'%' + " + nameWithPredicate + " + '%'\"/>";
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + ",typeHandler=" + typeHandlerClass + "}";
                        else
                            value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + "}";
                        break;
                    case NL:
                        value = "and " + camelToUnderlineName + " is null";
                        break;
                    case NN:
                        value = "and " + camelToUnderlineName + " is not null";
                        break;
                    case IN:
                        if(StringUtils.isNotBlank(typeHandlerClass))
                            value =
                                    "<if test=\"" + nameWithPredicate + ".length != 0\">\r\n"
                                            + "\t\t\t\tand " + camelToUnderlineName + " in\r\n"
                                            + "\t\t\t\t<foreach collection=\"" + nameWithPredicate + "\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\r\n"
                                            + "\t\t\t\t#{item" + ",typeHandler=" + typeHandlerClass + "}\r\n"
                                            + "\t\t\t\t</foreach>\r\n"
                                            + "\t\t\t\t</if>\r\n"
                                            + "\t\t\t\t<if test=\"" + nameWithPredicate + ".length == 0\">\r\n"
                                            + "\t\t\t\tand 1 = 2\r\n"
                                            + "\t\t\t\t</if>"
                                    ;
                        else
                            value =
                                    "<if test=\"" + nameWithPredicate + ".length != 0\">\r\n"
                                            + "\t\t\t\tand " + camelToUnderlineName + " in\r\n"
                                            + "\t\t\t\t<foreach collection=\"" + nameWithPredicate + "\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\r\n"
                                            + "\t\t\t\t#{item}\r\n"
                                            + "\t\t\t\t</foreach>\r\n"
                                            + "\t\t\t\t</if>\r\n"
                                            + "\t\t\t\t<if test=\"" + nameWithPredicate + ".length == 0\">\r\n"
                                            + "\t\t\t\tand 1 = 2\r\n"
                                            + "\t\t\t\t</if>"
                                    ;
                        break;
                    default:
                        break;
                }
                meta.getQuerys().put(name + predicate, value);
            }
        }
    }

    private void parseBasic(Class<?> klass, MybatisXmlMeta meta) {
        Arrays.asList(klass.getDeclaredFields()).stream().filter(ClassHelper::isNotStaticField).forEach(field -> {
            Column column = field.getDeclaredAnnotation(Column.class);
            String columnName = null;
            if (column != null) {
                if (StringUtils.isNotBlank(column.name())) {
                    columnName = column.name();
                }
            }
            String name = field.getName();
            Version version = field.getDeclaredAnnotation(Version.class);
            if (version != null) {
                if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(Integer.class)) {
                    if (StringUtils.isNotBlank(columnName)) {
                        meta.setVersion(columnName);
                    } else {
                        meta.setVersion(name);
                    }
                } else
                    throw new RuntimeException("version field type please use int or integer");
            } else {
                MybatisMappingMeta mappingMeta = new MybatisMappingMeta();
                String colName = StringUtils.isNotBlank(columnName) ? columnName : StringUtils.camelToUnderline(name);
                mappingMeta.setRawColumn(colName);
                if (config.isEscapeColumn()) {
                    mappingMeta.setColumn("`" + colName + "`");
                } else {
                    mappingMeta.setColumn(colName);
                }
                mappingMeta.setProperty(name);
                String typeHandlerClass = resolveTypeHandler(field);
                if(StringUtils.isNotBlank(typeHandlerClass)){
                    mappingMeta.setTypeHandler(typeHandlerClass);
                    mappingMeta.setJavaType(field.getType().getName());
                }

                if (field.getType().getName().toUpperCase().contains("Date".toUpperCase())) {
                    Temporal dateType = field.getDeclaredAnnotation(Temporal.class);
                    if (dateType != null) {
                        TemporalType value = dateType.value();
                        if (value != null)
                            if (value.equals(TemporalType.DATE))
                                mappingMeta.setJdbcType("DATE");
                            else if (value.equals(TemporalType.TIMESTAMP))
                                mappingMeta.setJdbcType("TIMESTAMP");
                            else if (value.equals(TemporalType.TIME))
                                mappingMeta.setJdbcType("TIME");
                    }
                }

                meta.getMappingMetas().add(mappingMeta);
            }

        });
    }

    private String parseEnum(Field field) {
        Enumerated enumerated = field.getDeclaredAnnotation(Enumerated.class);
        if (enumerated != null) {
            EnumType value = enumerated.value();
            if (EnumType.STRING.equals(value))
                return "org.apache.ibatis.type.EnumTypeHandler";
        }
        if (config.isUseEnumOrdinalTypeHandlerByDefault()) {
            return "org.apache.ibatis.type.EnumOrdinalTypeHandler";
        } else {
            return null;
        }
    }

    private String resolveTypeHandler(Field field){
        TypeHandler typeHandler = field.getDeclaredAnnotation(TypeHandler.class);
        if (typeHandler != null) {
            Class typeHandlerClass = typeHandler.value();
            if(org.apache.ibatis.type.TypeHandler.class.isAssignableFrom(typeHandlerClass)){
                return typeHandlerClass.getTypeName();
            }else
                throw new RuntimeException("typeHandler must be implement the interface org.apache.ibatis.type.TypeHandler");
        }
        if(field.getType().isEnum()){

            return parseEnum(field);
        }
        return null;
    }

    class MyEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringReader(""));
        }
    }

}

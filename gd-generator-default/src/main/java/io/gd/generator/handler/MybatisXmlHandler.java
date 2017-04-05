package io.gd.generator.handler;

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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Version;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisXmlHandler extends ScopedHandler<MybatisXmlMeta> {

	private boolean useGeneratedKeys;

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
					if (!(id == "delete".intern() || id == "insert".intern() || id == "update".intern() || id == "findOne".intern() || id == "findAll".intern()
							|| id == "baseResultMap".intern() || id == "merge".intern() || id == "count".intern())) {
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
		String trableName = ClassHelper.resolveTableName(entityClass);
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
		parsed.setUseGeneratedKeys(this.useGeneratedKeys);
		return parsed;
	}

	@Override
	protected void write(MybatisXmlMeta merged, Class<?> entityClass) throws Exception {
		Map<String, Object> model = new HashMap<>();
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
		String camelToUnderlineName = StringUtils.camelToUnderline(name);
		Query query = field.getAnnotation(Query.class);
		String value = null;
		Predicate[] predicates = null;
		if (query != null && (predicates = query.value()) != null) {
			for (Predicate predicate : predicates) {
				String nameWithPredicate = name + predicate.toString();
				String bind = null;
				switch (predicate) {
					case EQ:
						if (field.getType().isEnum())
							value = "and " + camelToUnderlineName + " = #{" + nameWithPredicate + ",typeHandler=" + this.parseEnum(field) + "}";
						else
							value = "and " + camelToUnderlineName + " = #{" + nameWithPredicate + "}";
						break;
					case NEQ:
						if (field.getType().isEnum())
							value = "and " + camelToUnderlineName + " != #{" + nameWithPredicate + ",typeHandler=" + this.parseEnum(field) + "}";
						else
							value = "and " + camelToUnderlineName + " != #{" + nameWithPredicate + "}";
						break;
					case GT:
						value = "and " + camelToUnderlineName + " &gt; #{" + nameWithPredicate + "}";
						break;
					case GTE:
						value = "and " + camelToUnderlineName + " &gt;= #{" + nameWithPredicate + "}";
						break;
					case LT:
						value = "and " + camelToUnderlineName + " &lt; #{" + nameWithPredicate + "}";
						break;
					case LTE:
						value = "and " + camelToUnderlineName + " &lt;= #{" + nameWithPredicate + "}";
						break;
					case EW:
						bind = "<bind name=\"" + nameWithPredicate + "\" value=\"'%' + " + nameWithPredicate + "\"/>";
						value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + "}";
						break;
					case SW:
						bind = "<bind name=\"" + nameWithPredicate + "\" value=\"" + nameWithPredicate + " + '%'\"/>";
						value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + "}";
						break;
					case LK:
						bind = "<bind name=\"" + nameWithPredicate + "\" value=\"'%' + " + nameWithPredicate + " + '%'\"/>";
						value = bind + " and " + camelToUnderlineName + " like #{" + nameWithPredicate + "}";
						break;
					case NL:
						value = "and " + camelToUnderlineName + " is null";
						break;
					case NN:
						value = "and " + camelToUnderlineName + " is not null";
						break;
					case IN:
						if (field.getType().isEnum())
							value =
									"<if test=\"" + nameWithPredicate + ".length != 0\">\r\n"
											+ "\t\t\t\tand " + camelToUnderlineName + " in\r\n"
											+ "\t\t\t\t<foreach collection=\"" + nameWithPredicate + "\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\r\n"
											+ "\t\t\t\t#{item" + ",typeHandler=" + this.parseEnum(field) + "}\r\n"
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
		Arrays.asList(klass.getDeclaredFields()).stream().filter(ClassHelper::isNotStaticField).forEach((field) -> {
			String name = field.getName();
			Version version = field.getDeclaredAnnotation(Version.class);
			if (version != null) {
				if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(Integer.class)) {
					meta.setVersion(name);
				} else
					throw new RuntimeException("version field type please use int or integer");
			} else {
				MybatisMappingMeta mappingMeta = new MybatisMappingMeta();
				mappingMeta.setColumn(StringUtils.camelToUnderline(name));
				mappingMeta.setProperty(name);
				if (field.getType().isEnum()) {
					mappingMeta.setEnumHander(parseEnum(field));
					mappingMeta.setJavaType(field.getType().getName());
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
		return "org.apache.ibatis.type.EnumOrdinalTypeHandler";

	}

	class MyEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new StringReader(""));
		}
	}

}

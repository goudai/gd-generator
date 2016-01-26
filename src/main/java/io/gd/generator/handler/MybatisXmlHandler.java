package io.gd.generator.handler;

import freemarker.template.Template;
import io.gd.generator.config.Config;
import io.gd.generator.context.MybatisContext;
import io.gd.generator.meta.mybatis.xml.MybatisMappingMeta;
import io.gd.generator.meta.mybatis.xml.MybatisXmlMeta;
import io.gd.generator.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Version;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class MybatisXmlHandler extends AbstractHandler<MybatisXmlMeta, MybatisContext> {

	@Override
	protected void preRead(MybatisContext context) throws Exception {
		File file = new File(context.getXmlPath() + File.separator + context.getEntityClass().getSimpleName() + "Mapper.xml");
		context.setXmlFile(file);
	}

	@Override
	protected MybatisXmlMeta read(MybatisContext context) throws Exception {
		File file = context.getXmlFile();
		MybatisXmlMeta meta = new MybatisXmlMeta();
		if(file.exists()) {
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
	protected MybatisXmlMeta parse(MybatisContext context) throws Exception {
		Class<?> entityClass = context.getEntityClass();
		Config config = context.getConfig();
		Table table = entityClass.getAnnotation(Table.class);
		String mapperName = context.getConfig().getMybatisMapperPackage() + "." + entityClass.getSimpleName() + "Mapper";
		MybatisXmlMeta meta = new MybatisXmlMeta();
		meta.setMapperName(mapperName);
		meta.setModel(entityClass.getName());
		@SuppressWarnings("unused")
		String trableName = table.name(); // TODO 通过注解获取表名
		meta.setTable(StringUtils.camelToUnderline(entityClass.getSimpleName()).replaceFirst("\\_", " "));
		meta.setSimpleName(entityClass.getSimpleName());
		parseBasic(entityClass, meta);
		Class<?> queryModelClass = context.getQueryModelClass();

		if (queryModelClass != null) {
			meta.setQuery(config.getQueryModelPackage() + "." + queryModelClass.getSimpleName());
			Arrays.asList(queryModelClass.getDeclaredFields()).stream().filter(this::filterSerialVersionUID).forEach((field) -> {
				parseQueryModel(meta, field);
			});
		}
		return meta;
	}

	@Override
	protected MybatisXmlMeta merge(MybatisXmlMeta parsed, MybatisXmlMeta read, MybatisContext context) throws Exception {
		if(read != null) {
			parsed.setOtherMappings(read.getOtherMappings());
		}
		return parsed;
	}

	@Override
	protected void write(MybatisXmlMeta merged, MybatisContext context) throws Exception {
		StringWriter out = new StringWriter();
		Template template = context.getFreemarkerConfiguration().getTemplate("mybatisXml.ftl");

		Map<String, Object> model = new HashMap<>();
		model.put("mxm", merged);
		template.process(model, out);
		String mapperString = out.toString();

		File file = context.getXmlFile();
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		try (FileOutputStream os = new FileOutputStream(file)) {
			os.write(mapperString.getBytes());
		}

	}

	@Override
	protected void postWrite(MybatisContext context) throws Exception {
		// DO NOTING
	}

	/**
	 * <if test="registerTimeLT != null"> and register_time &lt;
	 * #{registerTimeLT} </if> nicknameLK = key
	 * 
	 * <if test="nicknameLK != null"> <bind name="nicknameLK"
	 * value="'%' + _parameter.getNicknameLK() + '%'"/> and nickname like
	 * #{nicknameLK} </if>
	 * 
	 * @param meta
	 * @param field
	 */
	private void parseQueryModel(MybatisXmlMeta meta, Field field) {
		String name = field.getName();
		String value = null;
		if (name.endsWith("NEQ")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("NEQ", ""));
			if (field.getType().isEnum())
				value = " and " + camelToUnderline + " != #{" + name + ",typeHandler=" + this.parseEnum(field) + "}";
			else
				value = " and " + camelToUnderline + " != #{" + name + "}";
		} else if (name.endsWith("EQ")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("EQ", ""));
			if (field.getType().isEnum())
				value = " and " + camelToUnderline + " = #{" + name + ",typeHandler=" + this.parseEnum(field) + "}";
			else
				value = " and " + camelToUnderline + " = #{" + name + "}";
		} else if (name.endsWith("GT")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("GT", ""));
			value = " and " + camelToUnderline + " &gt; #{" + name + "}";
		} else if (name.endsWith("GTE")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("GTE", ""));
			value = " and " + camelToUnderline + " &gt;= #{" + name + "}";
		} else if (name.endsWith("LT")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("LT", ""));
			value = " and " + camelToUnderline + " &lt; #{" + name + "}";
		} else if (name.endsWith("LTE")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("LTE", ""));
			value = " and " + camelToUnderline + " &lt;= #{" + name + "}";
		} else if (name.endsWith("NL")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("NL", ""));
			value = " and " + camelToUnderline + " is null #{" + name + "}";
		} else if (name.endsWith("NN")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("NN", ""));
			value = " and " + camelToUnderline + " is not null #{" + name + "}";
		} else if (name.endsWith("LK")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("LK", ""));
			String bind = "<bind name=\"" + name + "\" value=\"'%' + " + name + " + '%'\"/>";
			value = bind + " and " + camelToUnderline + " like #{" + name + "}";
		} else if (name.endsWith("SW")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("SW", ""));
			String bind = "<bind name=\"" + name + "\" value=\"" + name + " + '%'\"/>";
			value = bind + " and " + camelToUnderline + " like #{" + name + "}";
		} else if (name.endsWith("EW")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("EW", ""));
			String bind = "<bind name=\"" + name + "\" value=\"'%' + " + name + "\"/>";
			value = bind + " and " + camelToUnderline + " like #{" + name + "}";
		} else if (name.endsWith("IN")) {
			String camelToUnderline = StringUtils.camelToUnderline(name.replace("IN", ""));
			value = " and " + camelToUnderline + " in\r\n" + "\t\t\t\t<foreach collection=\"" + name
					+ "\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\r\n\t\t\t\t#{item}\r\n\t\t\t\t</foreach>";
		} else
			return;

		meta.getQuerys().put(name, value);
	}

	private void parseBasic(Class<?> klass, MybatisXmlMeta meta) {
		Arrays.asList(klass.getDeclaredFields()).stream().filter(this::filterSerialVersionUID).forEach((field) -> {
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

package io.gd.generator.handler;

import freemarker.template.Template;
import io.gd.generator.context.MybatisContext;
import io.gd.generator.meta.mybatis.mapper.MybatisMapperMeta;
import io.gd.generator.util.FileUtils;
import io.gd.generator.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisMapperHandler extends AbstractHandler<MybatisMapperMeta, MybatisContext> {

	@Override
	void preRead(MybatisContext context) throws Exception {
		File file = new File(context.getMapperPath() + File.separator + context.getEntityClass().getSimpleName() + "Mapper.java");
		context.setMapperFile(file);
	}

	@Override
	MybatisMapperMeta read(MybatisContext context) throws Exception {
		File file = context.getMapperFile();
		String string = FileUtils.read(file);
		MybatisMapperMeta meta = new MybatisMapperMeta();
		List<String> otherMethods = new ArrayList<>();
		if (StringUtils.isNotBank(string)) {
			String[] split2 = string.split("\\{");
			String[] split = split2[1].split("\\}")[0].split(";");
			Arrays.asList(split).forEach(
					(m) -> {
						if (!(m.contains("insert(") || m.contains("update(") || m.contains("findOne(") || m.contains("findAll(") || m.contains("merge(")
								|| m.contains("count(") || m.contains("delete("))) {
							if (StringUtils.isNotBank(m.trim())) {
								if (!";".equals(m.trim()) && !"\\t".equals(m.trim())) {
									otherMethods.add(m.trim());
								}
							}
						}
					});
		}
		meta.setOtherMethods(otherMethods);
		return meta;
	}

	@Override
	MybatisMapperMeta parse(MybatisContext context) throws Exception {
		Class<?> entityClass = context.getEntityClass();
		Class<?> queryModelClass = context.getQueryModelClass();
		MybatisMapperMeta meta = new MybatisMapperMeta();
		if (queryModelClass != null) {
			meta.setHasQueryModel(true);
			meta.setQueryModel(queryModelClass.getName());
			meta.setSimpleQuery(queryModelClass.getSimpleName());
		}
		meta.setModelName(entityClass.getName());
		meta.setSimpleName(entityClass.getSimpleName());
		return meta;
	}

	@Override
	MybatisMapperMeta merge(MybatisMapperMeta parsed, MybatisMapperMeta read, MybatisContext context) throws Exception {
		parsed.setOtherMethods(read.getOtherMethods());
		return parsed;
	}

	@Override
	void write(MybatisMapperMeta merged, MybatisContext context) throws Exception {
		StringWriter out = new StringWriter();
		Template template = context.getFreemarkerConfiguration().getTemplate("mybatisMapper.ftl");

		Map<String, Object> model = new HashMap<>();
		model.put("basePackage", context.getConfig().getMybatisMapperPackage());
		model.put("mmm", merged);
		template.process(model, out);
		String mapperString = out.toString();

		File file = context.getMapperFile();
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		try (FileOutputStream os = new FileOutputStream(file)) {
			os.write(mapperString.getBytes());
		}
	}

	@Override
	void postWrite(MybatisContext context) throws Exception {

	}

}

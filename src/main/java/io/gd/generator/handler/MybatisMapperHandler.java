package io.gd.generator.handler;

import io.gd.generator.context.MybatisContext;
import io.gd.generator.meta.mybatis.mapper.MybatisMapperMeta;
import io.gd.generator.util.FileUtils;
import io.gd.generator.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MybatisMapperHandler extends AbstractHandler<MybatisMapperMeta, MybatisContext> {

	@Override
	protected void preRead(MybatisContext context) throws Exception {
		File file = new File(context.getMapperPath() + File.separator + context.getEntityClass().getSimpleName() + "Mapper.java");
		context.setMapperFile(file);
	}

	@Override
	protected MybatisMapperMeta read(MybatisContext context) throws Exception {
		File file = context.getMapperFile();
		String string = FileUtils.read(file);
		MybatisMapperMeta meta = new MybatisMapperMeta();
		if (StringUtils.isNotBank(string)) {
			String[] split2 = string.split("\\{");
			String[] split = split2[1].split("\\}")[0].split(";");
			Arrays.asList(split).forEach(
					(m) -> {
						if (!(m.contains("insert(") || m.contains("update(") || m.contains("findOne(") || m.contains("findAll(") || m.contains("merge(")
								|| m.contains("count(") || m.contains("delete("))) {
							String mTrim = m.trim();
							if (StringUtils.isNotBank(mTrim) && !";".equals(mTrim)) {
								meta.getOtherMethods().add(m.trim());
							}
						}
					});
			String split3 = split2[0];
			Pattern pattern = Pattern.compile("import[\\s]+([\\w\\.]+)[\\s]*;");
			Matcher m = pattern.matcher(split3);
			while(m.find()) {
				String g1 = m.group(1);
				if(StringUtils.isNotBank(g1)) {
					meta.getOtherImports().add(g1);
				}
			}
		}
		return meta;
	}

	@Override
	protected MybatisMapperMeta parse(MybatisContext context) throws Exception {
		Class<?> entityClass = context.getEntityClass();
		Class<?> queryModelClass = context.getQueryModelClass();
		MybatisMapperMeta meta = new MybatisMapperMeta();
		if (queryModelClass != null) {
			meta.setHasQueryModel(true);
			meta.setQueryModelName(queryModelClass.getName());
			meta.setQueryModelSimpleName(queryModelClass.getSimpleName());
		}
		meta.setEntityName(entityClass.getName());
		meta.setEntitySimpleName(entityClass.getSimpleName());
		return meta;
	}

	@Override
	protected MybatisMapperMeta merge(MybatisMapperMeta parsed, MybatisMapperMeta read, MybatisContext context) throws Exception {
		if(read != null) {
			parsed.setOtherMethods(read.getOtherMethods());
			boolean hasQueryModel = parsed.isHasQueryModel();
			for(String otherImport : read.getOtherImports()) {
				if(otherImport.equals("org.apache.ibatis.annotations.Param")) {
					continue;
				}
				if(otherImport.equals(parsed.getEntityName())) {
					continue;
				}
				if(hasQueryModel && otherImport.equals(parsed.getQueryModelName())) {
					continue;
				}
				if(otherImport.equals("java.util.List")) {
					continue;
				}
				parsed.getOtherImports().add(otherImport);
			}
			
		}
		return parsed;
	}

	@Override
	protected void write(MybatisMapperMeta merged, MybatisContext context) throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("basePackage", context.getConfig().getMybatisMapperPackage());
		model.put("mmm", merged);
		String mapper = renderTemplate("mybatisMapper", model, context);

		File file = context.getMapperFile();
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		try (FileOutputStream os = new FileOutputStream(file)) {
			os.write(mapper.getBytes());
		}
	}

	@Override
	protected void postWrite(MybatisContext context) throws Exception {

	}

}

package io.gd.generator.handler;

import io.gd.generator.api.query.QueryModel;
import io.gd.generator.meta.mybatis.MybatisMapperMeta;
import io.gd.generator.util.ConfigChecker;
import io.gd.generator.util.FileUtils;
import io.gd.generator.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MybatisMapperHandler extends ScopedHandler<MybatisMapperMeta> {
	
	private String getMapperFilePath(Class<?> entityClass) {
		return config.getMybatisMapperPath() + File.separator + entityClass.getSimpleName() + "Mapper.java";
	}
	
	@Override
	protected void init() throws Exception {
		super.init();
		ConfigChecker.notBlank(config.getMybatisMapperPackage(), "config mybatisMapperPackage is miss");
		ConfigChecker.notBlank(config.getMybatisMapperPath(), "config mybatisMapperPath is miss");
		
		String mapperPath = config.getMybatisMapperPath();
	
		/* 初始化文件夹 */
		File mapperPathDir = new File(mapperPath);
		if (!mapperPathDir.exists()) {
			mapperPathDir.mkdirs();
		} else if (!mapperPathDir.isDirectory()) {
			throw new IllegalArgumentException("mapperPath is not a directory");
		}
		
	}
	
	@Override
	protected void preRead(Class<?> entityClass) throws Exception {
		
	}

	@Override
	protected MybatisMapperMeta read(Class<?> entityClass) throws Exception {
		String string = FileUtils.read(getMapperFilePath(entityClass));
		MybatisMapperMeta meta = new MybatisMapperMeta();
		if (StringUtils.isNotBlank(string)) {
			String[] split2 = string.split("\\{");
			String[] split = split2[1].split("\\}")[0].split(";");
			Arrays.asList(split).forEach(
					(m) -> {
						if (!(m.contains("insert(") || m.contains("update(") || m.contains("findOne(") || m.contains("findAll(") || m.contains("merge(")
								|| m.contains("count(") || m.contains("delete("))) {
							String mTrim = m.trim();
							if (StringUtils.isNotBlank(mTrim) && !";".equals(mTrim)) {
								meta.getOtherMethods().add(m.trim());
							}
						}
					});
			String split3 = split2[0];
			Pattern pattern = Pattern.compile("import[\\s]+([\\w\\.]+)[\\s]*;");
			Matcher m = pattern.matcher(split3);
			while(m.find()) {
				String g1 = m.group(1);
				if(StringUtils.isNotBlank(g1)) {
					meta.getOtherImports().add(g1);
				}
			}
		}
		return meta;
	}

	@Override
	protected MybatisMapperMeta parse(Class<?> entityClass) throws Exception {
		QueryModel queryModel = entityClass.getAnnotation(QueryModel.class);
		
		String entityClassSimpleName = entityClass.getSimpleName();
		MybatisMapperMeta meta = new MybatisMapperMeta();
		if (queryModel != null) {
			meta.setHasQueryModel(true);
			meta.setQueryModelName(config.getQueryModelPackage() + "." + entityClassSimpleName + config.getQueryModelSuffix());
			meta.setQueryModelSimpleName(entityClassSimpleName + config.getQueryModelSuffix());
		}
		meta.setEntityName(entityClass.getName());
		meta.setEntitySimpleName(entityClass.getSimpleName());
		return meta;
	}

	@Override
	protected MybatisMapperMeta merge(MybatisMapperMeta parsed, MybatisMapperMeta read, Class<?> entityClass) throws Exception {
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
		parsed.setMapperPackage(config.getMybatisMapperPackage());
		return parsed;
	}

	@Override
	protected void write(MybatisMapperMeta merged, Class<?> entityClass) throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("meta", merged);
		String mapper = renderTemplate("mybatisMapper", model);
		File file = new File(getMapperFilePath(entityClass));

		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		try (FileOutputStream os = new FileOutputStream(file)) {
			os.write(mapper.getBytes());
		}
	}

	@Override
	protected void postWrite(Class<?> entityClass) throws Exception {

	}

}

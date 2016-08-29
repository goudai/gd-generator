package io.gd.generator.handler;

import io.gd.generator.api.query.Predicate;
import io.gd.generator.api.query.Query;
import io.gd.generator.api.query.QueryModel;
import io.gd.generator.meta.querymodel.QueryModelMeta;
import io.gd.generator.meta.querymodel.QueryModelMeta.QueryModelField;
import io.gd.generator.util.ClassHelper;
import io.gd.generator.util.ConfigChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryModelHandler extends ScopedHandler<QueryModelMeta> {

	@Override
	protected void init() throws Exception {
		super.init();
		ConfigChecker.notBlank(config.getQueryModelPackage(), "config queryModelPackage is miss");
		ConfigChecker.notBlank(config.getQueryModelPath(), "config queryModelPath is miss");
		ConfigChecker.notBlank(config.getQueryModelSuffix(), "config queryModelSuffix is miss");
		
		/* 初始化文件夹 */
		File path = new File(config.getQueryModelPath());
		if (!path.exists()) {
			path.mkdirs();
		} else if (!path.isDirectory()) {
			throw new IllegalArgumentException("queryModelPath is not a directory");
		}

	}
	
	private String getQueryModelFilePath(Class<?> entityClass) {
		return config.getQueryModelPath() + File.separator + entityClass.getSimpleName() + config.getQueryModelSuffix() + ".java";
	}
	
	@Override
	protected void preRead(Class<?> entityClass) throws Exception {
	}

	@Override
	protected QueryModelMeta read(Class<?> entityClass) throws Exception {
		return null;
	}

	@Override
	protected QueryModelMeta parse(Class<?> entityClass) throws Exception {
		QueryModel queryModel = entityClass.getAnnotation(QueryModel.class);
		if (queryModel == null) {
			return null;
		} else {
			QueryModelMeta meta = new QueryModelMeta();
			meta.setType(entityClass.getSimpleName() + config.getQueryModelSuffix());
			meta.setQueryModelPackage(config.getQueryModelPackage());
			meta.setUseLombok(config.isUseLombok());
			
			ClassHelper.getFields(entityClass).stream().filter(ClassHelper::isNotStaticField)
				.forEach(v -> {
					meta.getFieldNames().add(v.getName());
					Query query = v.getAnnotation(Query.class);
					if (query != null) {
						for (Predicate predicate : query.value()) {
							QueryModelField queryModelField = new QueryModelField();
							queryModelField.setType(v.getType().getSimpleName());
							queryModelField.setName(v.getName() + predicate);
							if (predicate == Predicate.IN) {
								queryModelField.setArray(true);
							}
							meta.getQueryModelFields().add(queryModelField);
							meta.getImportFullTypes().add(v.getType().getName());
						}
					}
					
				});
			
			return meta;
		}
	}

	@Override
	protected QueryModelMeta merge(QueryModelMeta parsed, QueryModelMeta read, Class<?> entityClass) throws Exception {
		if (parsed != null) {
			parsed.setImportFullTypes(parsed.getImportFullTypes().stream().filter(v -> !v.startsWith("java.lang.")).collect(Collectors.toSet()));
		}
		return parsed;
	}

	@Override
	protected void write(QueryModelMeta merged, Class<?> entityClass) throws Exception {
		if (merged != null) {
			Map<String, Object> model = new HashMap<>();
			model.put("meta", merged);
			String mapper = renderTemplate("queryModel", model);
			File file = new File(getQueryModelFilePath(entityClass));
			
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			try (FileOutputStream os = new FileOutputStream(file)) {
				os.write(mapper.getBytes());
			}
		}
	}

	@Override
	protected void postWrite(Class<?> entityClass) throws Exception {

	}

}

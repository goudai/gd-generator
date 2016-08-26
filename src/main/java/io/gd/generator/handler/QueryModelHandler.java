package io.gd.generator.handler;

import io.gd.generator.api.Predicate;
import io.gd.generator.api.Query;
import io.gd.generator.api.QueryModel;
import io.gd.generator.meta.querymodel.QueryModelMeta;
import io.gd.generator.meta.querymodel.QueryModelMeta.QueryModelField;
import io.gd.generator.util.ClassHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryModelHandler extends ScopedHandler<QueryModelMeta> {

	private String getQueryModelFilePath(Class<?> entityClass) {
		return config.getQueryModelPackage() + File.separator + entityClass.getSimpleName() + "Mapper.java";
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
					Query query = v.getAnnotation(Query.class);
					if (query != null) {
						for (Predicate predicate : query.predicate()) {
							QueryModelField queryModelField = new QueryModelField();
							queryModelField.setType(v.getType().getSimpleName());
							queryModelField.setName(v.getName() + predicate);
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

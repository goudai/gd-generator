package io.gd.generator.handler;

import io.gd.generator.api.QueryModel;
import io.gd.generator.meta.querymodel.QueryModelMeta;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

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
		
		String entityClassSimpleName = entityClass.getSimpleName();
		QueryModelMeta meta = new QueryModelMeta();
		
		return meta;
	}

	@Override
	protected QueryModelMeta merge(QueryModelMeta parsed, QueryModelMeta read, Class<?> entityClass) throws Exception {
		return parsed;
	}

	@Override
	protected void write(QueryModelMeta merged, Class<?> entityClass) throws Exception {
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

	@Override
	protected void postWrite(Class<?> entityClass) throws Exception {

	}

}

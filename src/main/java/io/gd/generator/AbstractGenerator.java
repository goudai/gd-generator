package io.gd.generator;

import io.gd.generator.handler.Handler;
import io.gd.generator.util.ClassHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;

public class AbstractGenerator implements Generator {

	Logger logger = LoggerFactory.getLogger(AbstractGenerator.class);

	protected Configuration freemarkerConfiguration;

	protected Config config;

	protected List<Handler<?>> handlers;

	public AbstractGenerator(Config config) {
		this.freemarkerConfiguration = null;
		this.config = config;
		this.handlers = new ArrayList<>();
	}

	@Override
	public void generate() {
		/* 获取所有 entity */
		Set<Class<?>> entityClasses = ClassHelper.getClasses(config.getEntityPackage());
		/* 获取所有 query model */
		Map<String, Class<?>> queryModelClasses = ClassHelper.getQuerysClasses(config.getQueryModelPackage());
		/* 遍历生成 */
		entityClasses.parallelStream().forEach(entityClass -> {

			if (entityClass.getDeclaredAnnotation(Table.class) != null) {

				try {
					/* 生成mapper */
					generateOne(entityClass, queryModelClasses.get(entityClass.getSimpleName() + config.getQueryModelSuffix()));
				} catch (Exception e) {
					logger.error("generate " + entityClass.getName() + " error", e);
				}
			} else {
				logger.info("skip " + entityClass.getName());
			}
		});

	}

	protected void generateOne(Class<?> entityClass, Class<?> queryModelClass) throws Exception {
		for (Handler<?> handler : handlers) {
			Context context = new Context();
			context.setConfig(config);
			context.setEntityClass(entityClass);
			context.setQueryModelClass(queryModelClass);
			context.setFreemarkerConfiguration(freemarkerConfiguration);
			handle(handler, context);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handle(Handler handler, Context context) throws Exception {
		Object read = handler.read(context);
		Object parsed = handler.parse(context);
		Object meta = handler.merge(parsed, read, context);
		handler.write(meta, context);
	}

}

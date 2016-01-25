package io.gd.generator;

import io.gd.generator.handler.Handler;
import io.gd.generator.util.ClassHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Version;

public abstract class AbstractGenerator implements Generator {

	static final Logger logger = LoggerFactory.getLogger(AbstractGenerator.class);

	protected Configuration freemarkerConfiguration;

	protected Config config;

	protected List<Handler> handlers;

	public AbstractGenerator(Config config) {
		this.config = config;
		this.handlers = new ArrayList<>();
	}
	
	protected void init() throws Exception {
		freemarkerConfiguration =  new Configuration(new Version("2.3.0"));
		freemarkerConfiguration.setDefaultEncoding("UTF-8");
		freemarkerConfiguration.setDirectoryForTemplateLoading(new File(AbstractGenerator.class.getClassLoader().getResource("template").getPath()));
	}
	
	protected void destroy() throws Exception {

	}

	@Override
	public void generate() {
		try {
			init();
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
		} catch(Exception e) {
			logger.error("generate error", e);
		}	finally {
			try {
				destroy();
			} catch (Exception e) {
				logger.error("destroy error", e);
			}
		}
	}

	protected void generateOne(Class<?> entityClass, Class<?> queryModelClass) throws Exception {
		for (Handler handler : handlers) {
			Context context = new Context();
			context.setConfig(config);
			context.setEntityClass(entityClass);
			context.setQueryModelClass(queryModelClass);
			context.setFreemarkerConfiguration(freemarkerConfiguration);
			handler.handle(context);
		}
	}

}

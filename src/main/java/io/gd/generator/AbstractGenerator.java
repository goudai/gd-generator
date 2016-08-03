package io.gd.generator;

import freemarker.template.Configuration;
import freemarker.template.Version;
import io.gd.generator.config.Config;
import io.gd.generator.context.Context;
import io.gd.generator.context.GenLog;
import io.gd.generator.handler.Handler;
import io.gd.generator.util.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractGenerator<T extends Context> implements Generator {

	static final Logger logger = LoggerFactory.getLogger(AbstractGenerator.class);

	protected Configuration freemarkerConfiguration;

	protected Config config;

	protected List<Handler<?>> handlers;
	
	protected GenLog genLog;
	
	protected Class<T> contextClass;
	
	@SuppressWarnings("unchecked")
	public AbstractGenerator(Config config) {
		this.config = config;
		this.handlers = new ArrayList<>();
		contextClass = (Class<T>)(((ParameterizedType)(getClass().getGenericSuperclass())).getActualTypeArguments()[0]);
	}

	public void init() throws Exception {
		freemarkerConfiguration = new Configuration(new Version(config.getFreemakerVersion()));
		freemarkerConfiguration.setDefaultEncoding(config.getDefaultEncoding());
		freemarkerConfiguration.setClassForTemplateLoading(getClass(),"/"+config.getTemplate());
		genLog = new GenLog(config.getGenLogFile());
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
			// entityClasses.parallelStream().forEach(entityClass -> {
			entityClasses.stream().forEach(entityClass -> {
				if (entityClass.getDeclaredAnnotation(Table.class) != null) {
					try {
						/* 生成mapper */
						generateOne(entityClass, queryModelClasses.get(entityClass.getSimpleName() + config.getQueryModelSuffix()));
						logger.info("generate " + entityClass.getName() + " success");
					} catch (Exception e) {
						logger.error("generate " + entityClass.getName() + " error", e);
					}
				} else {
					logger.info("generate " + entityClass.getName() + " skipped");
				}
			});
		} catch (Exception e) {
			logger.error("generate error", e);
		} finally {
			try {
				genLog.flush();
			} catch (Exception e) {
				logger.error("flush genLog error", e);
			}
			try {
				destroy();
			} catch (Exception e) {
				logger.error("destroy error", e);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void generateOne(Class<?> entityClass, Class<?> queryModelClass) throws Exception {
		T context = contextClass.newInstance();
		initContext(context);
		context.setEntityClass(entityClass);
		context.setQueryModelClass(queryModelClass);
		for (Handler handler : handlers) {
			handler.handle(context);
		}
	}

	protected void initContext(T context) {
		context.setGenLog(genLog);
		context.setFreemarkerConfiguration(freemarkerConfiguration);
		context.setConfig(config);
	}

}

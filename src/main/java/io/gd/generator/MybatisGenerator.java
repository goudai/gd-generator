package io.gd.generator;

import io.gd.generator.config.Config;
import io.gd.generator.context.MybatisContext;
import io.gd.generator.handler.MybatisMapperHandler;
import io.gd.generator.handler.MybatisXmlHandler;
import io.gd.generator.util.StringUtils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisGenerator extends MysqlGenerator<MybatisContext> {

	static final Logger logger = LoggerFactory.getLogger(MybatisGenerator.class);

	private String xmlPath;

	private String mapperPath;

	public MybatisGenerator(Config config) {
		super(config);
		handlers.add(new MybatisMapperHandler());
		handlers.add(new MybatisXmlHandler());
	}

	@Override
	protected void init() throws Exception {
		super.init();
		if(StringUtils.isBank(config.getMybatisMapperPackage()) || StringUtils.isBank(config.getMybatisXmlPackage())) {
			throw new IllegalArgumentException("mybatisMapperPackage or mybatisXmlPackage config error");
		}
		if (StringUtils.isNotBank(config.getMybatisMapperPath()) && StringUtils.isNotBank(config.getMybatisXmlPath())) {
			xmlPath = config.getMybatisXmlPath();
			mapperPath = config.getMybatisMapperPath();
		} else if (StringUtils.isNotBank(config.getJavaSrc()) && StringUtils.isNotBank(config.getResources())) {
			String projectPath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/target/classes/", "");
			xmlPath = (projectPath + config.getResources() + File.separator + (config.getMybatisXmlPackage().replace(".", File.separator))).replace("/",
					File.separator).replace("\\", File.separator);
			mapperPath = (projectPath + config.getJavaSrc() + File.separator + (config.getMybatisMapperPackage().replace(".", File.separator))).replace("/",
					File.separator).replace("\\", File.separator);
		} else {
			throw new IllegalArgumentException("xmlPath or mapperPath config error");
		}
		/* 初始化文件夹 */
		File xmlPathDir = new File(xmlPath);
		if (!xmlPathDir.exists()) {
			xmlPathDir.mkdirs();
		} else if (!xmlPathDir.isDirectory()) {
			throw new IllegalArgumentException("xmlPath is not a directory");
		}

		File mapperPathDir = new File(mapperPath);
		if (!mapperPathDir.exists()) {
			mapperPathDir.mkdirs();
		} else if (!mapperPathDir.isDirectory()) {
			throw new IllegalArgumentException("mapperPath is not a directory");
		}

	}

	@Override
	protected void initContext(MybatisContext context) {
		super.initContext(context);
		context.setXmlPath(xmlPath);
		context.setMapperPath(mapperPath);
	}

}

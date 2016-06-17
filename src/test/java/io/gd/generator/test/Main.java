package io.gd.generator.test;

import java.io.File;

import io.gd.generator.Generator;
import io.gd.generator.MybatisGenerator;
import io.gd.generator.config.Config;

public class Main {
	public static void main(String[] S) {
		Config config = new Config();
		config.setGenLogFile(System.getProperty("user.home") + File.pathSeparator + "gd-test.log");
		config.setUrl("jdbc:mysql://127.0.0.1/test");
		config.setEntityPackage("io.gd.generator.test.entity");
		config.setMybatisMapperPackage("io.gd.generator.test.mapper");
		config.setQueryModelPackage("io.gd.generator.test.model.query");
		config.setMybatisXmlPackage("io.gd.generator.test.mapping");
		config.setJavaSrc("/src/test/java");
		config.setResources("/src/test/resources");
		config.setUsername("root");
		config.setPassword("123456");
		Generator generator = new MybatisGenerator(config);
		generator.generate();
	}

}

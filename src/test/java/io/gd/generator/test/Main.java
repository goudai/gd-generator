package io.gd.generator.test;

import io.gd.generator.AbstractGenerator;
import io.gd.generator.MybatisGenerator;
import io.gd.generator.NodeGenerator;
import io.gd.generator.config.Config;
import io.gd.generator.config.NodeConfig;

import java.io.File;

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
		AbstractGenerator generator = new MybatisGenerator(config);
		NodeConfig nodeConfig = new NodeConfig();
		nodeConfig.setDistFile(new File("./dubbo.js"));
		nodeConfig.setDocFile(new File("./doc.js"));
		nodeConfig.setServicePackage("io.gd.generator.test.service");
		generator = new NodeGenerator(nodeConfig);
		generator.generate();
	}

}

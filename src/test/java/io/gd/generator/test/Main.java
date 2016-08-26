package com.sk;

import io.gd.generator.AbstractGenerator;
import io.gd.generator.Config;
import io.gd.generator.MybatisGenerator;

import java.io.File;

public class Main {
	public static void main(String[] S) {
		Config config = new Config();
		config.setGenLogFile(System.getProperty("user.home") + File.pathSeparator + "/gd-test.log");
		config.setUrl("jdbc:mysql://192.168.10.240/sk");
		config.setEntityPackage("com.sk.entity");
		config.setMybatisMapperPackage("com.sk.mapper");
		config.setQueryModelPackage("com.sk.model.query");
		config.setMybatisXmlPackage("com.sk.mapping");
		config.setJavaSrc("/Users/freeman/IdeaProjects/miziProjects/sk/sk-service/src/main/java");
		config.setResources("/Users/freeman/IdeaProjects/miziProjects/sk/sk-service-impl/src/main/resources");
		config.setUsername("root");
		config.setPassword("123456");
		AbstractGenerator generator = new MybatisGenerator(config);
//		NodeConfig nodeConfig = new NodeConfig();
//		nodeConfig.setDistFile(new File("./dubbo.js"));
//		nodeConfig.setDocFile(new File("./doc.js"));
//		nodeConfig.setServicePackage("com.sk.service");
//		generator = new NodeGenerator(nodeConfig);
		generator.generate();
	}

}

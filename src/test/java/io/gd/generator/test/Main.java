package io.gd.generator.test;

import io.gd.generator.Config;
import io.gd.generator.Generator;
import io.gd.generator.handler.QueryModelHandler;

import java.io.File;

public class Main {
	public static void main(String[] S) throws Exception {
		
		Config config = new Config();
		config.setGenLogFile(System.getProperty("user.home") + File.pathSeparator + "/gd-test.log");
		/*config.setUrl("jdbc:mysql://192.168.10.240/sk");
		config.setEntityPackage("com.sk.entity");
		config.setUsername("root");
		config.setPassword("123456");
		config.setMybatisMapperPackage("com.sk.mapper");
		config.setQueryModelPackage("com.sk.model.query");*/
		
		config.setEntityPackage("io.gd.generator.test.entity");
		
		
		config.setQueryModelPackage("io.gd.generator.test.model.query");
		config.setQueryModelPath("D:\\Work\\Workspace2016\\gd-generator\\src\\test\\java\\io\\gd\\generator\\test\\model\\query\\");

		
		Generator.generate(config, QueryModelHandler.class);
	}

}

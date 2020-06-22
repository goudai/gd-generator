package io.gd.generator.test;

import io.gd.generator.Config;
import io.gd.generator.Generator;
import io.gd.generator.handler.AllMappingMysqlHandler;
import io.gd.generator.handler.MybatisXmlHandler;
import io.gd.generator.handler.MysqlHandler;

public class Main {
	public static void main(String[] S) throws Exception {

		Config config = new Config();
		config.setGenLogFile("gd-test.log");
		config.setUseEnumOrdinalTypeHandlerByDefault(false);
		config.setEscapeColumn(true);
		config.setUrl("jdbc:mysql://192.168.1.11/test?charset=utf8");
		config.setEntityPackage("com.sk.entity");
		config.setUsername("test");
		config.setPassword("123456");
		config.setMybatisMapperPackage("com.sk.mapper");
		config.setQueryModelPackage("com.sk.model.query");

		config.setEntityPackage("io.gd.generator.test.entity");
		config.setUseLombok(false);
		config.setMybatisXmlPath("/tmp/gd/mapper");

		config.setQueryModelPackage("io.gd.generator.test.model.query");
		config.setQueryModelPath("/tmp/gd/xml");

		Generator.generate(config
//				,new VoHandler("io.gd.generator.test.vo", "/Users/freeman/IdeaProjects/gd-generator/gd-generator-default/src/test/java/io/gd/generator/test/vo", true)
//				,new NodeHandler("service.js","service.doc.json","io.gd.generator.test.service")
				, new AllMappingMysqlHandler()
		);
	}

}

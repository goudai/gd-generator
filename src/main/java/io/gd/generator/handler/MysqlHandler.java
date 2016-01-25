package io.gd.generator.handler;

import io.gd.generator.context.MybatisContext;
import io.gd.generator.meta.mysql.MysqlTableMeta;

//TODO
public class MysqlHandler extends AbstractHandler<MysqlTableMeta, MybatisContext> {

	@Override
	void preRead(MybatisContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	MysqlTableMeta read(MybatisContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	MysqlTableMeta parse(MybatisContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	MysqlTableMeta merge(MysqlTableMeta parsed, MysqlTableMeta read, MybatisContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void write(MysqlTableMeta merged, MybatisContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	void postWrite(MybatisContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}

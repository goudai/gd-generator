package io.gd.generator.handler;


public abstract  class ScopedHandler<T> extends AbstractHandler {
	
	@Override
	public void doHandleOne(Class<?> entityClass) throws Exception {
		try {
			preRead(entityClass); // 预处理
			T read = read(entityClass); // 读原有meta
			T parsed = parse(entityClass); // 解析meta
			T merged = merge(parsed, read, entityClass); // 合并
			write(merged, entityClass); // 写入
		} finally {
			postWrite(entityClass); // 后处理
		}
	}
	
	abstract protected void preRead(Class<?> entityClass) throws Exception;

	abstract protected T read(Class<?> entityClass) throws Exception;

	abstract protected T parse(Class<?> entityClass) throws Exception;

	abstract protected T merge(T parsed, T read, Class<?> entityClass) throws Exception;

	abstract protected void write(T merged, Class<?> entityClass) throws Exception;

	abstract protected void postWrite(Class<?> entityClass) throws Exception;
	

}

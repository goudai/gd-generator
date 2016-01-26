package io.gd.generator.handler;

import java.lang.reflect.Field;

import io.gd.generator.context.Context;

public abstract class AbstractHandler<T, S extends Context> implements Handler<S> {

	abstract protected void preRead(S context) throws Exception;

	abstract protected T read(S context) throws Exception;

	abstract protected T parse(S context) throws Exception;

	abstract protected T merge(T parsed, T read, S context) throws Exception;

	abstract protected void write(T merged, S context) throws Exception;

	abstract protected void postWrite(S context) throws Exception;

	@Override
	public void handle(S context) throws Exception {
		try {
			preRead(context);
			T read = read(context);
			T parsed = parse(context);
			T meta = merge(parsed, read, context);
			write(meta, context);
		} finally {
			postWrite(context);
		}

	}

	protected boolean filterSerialVersionUID(Field field) {
		return !(field.getName().equals("serialVersionUID"));
	}

}

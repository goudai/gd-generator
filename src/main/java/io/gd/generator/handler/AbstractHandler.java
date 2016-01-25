package io.gd.generator.handler;

import io.gd.generator.context.Context;

public abstract class AbstractHandler<T, S extends Context> implements Handler<S> {

	abstract void preRead(S context) throws Exception;

	abstract T read(S context) throws Exception;

	abstract T parse(S context) throws Exception;

	abstract T merge(T parsed, T read, S context) throws Exception;

	abstract void write(T merged, S context) throws Exception;

	abstract void postWrite(S context) throws Exception;

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
	
}

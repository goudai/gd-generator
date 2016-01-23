package io.gd.generator.handler;

import io.gd.generator.Context;

public abstract class AbstractHandler<T> implements Handler {

	protected void init(Context context) throws Exception {
		// TO NOTHING
	}

	protected T read(Context context) throws Exception {
		return null;
	}

	abstract T parse(Context context) throws Exception;

	protected T merge(T parsed, T read, Context context) throws Exception {
		return parsed;
	}

	abstract void write(T merged, Context context) throws Exception;

	protected void destroy(Context context) throws Exception {
		// TO NOTHING
	}

	@Override
	public void handle(Context context) throws Exception {
		try {
			init(context);
			T read = read(context);
			T parsed = parse(context);
			T meta = merge(parsed, read, context);
			write(meta, context);
		} finally {
			destroy(context);
		}

	}

}

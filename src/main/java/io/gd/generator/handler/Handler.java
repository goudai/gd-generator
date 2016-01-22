package io.gd.generator.handler;

import io.gd.generator.Context;

/* T: meta object or String */
public interface Handler<T> {

	T read(Context context) throws Exception;

	T parse(Context context) throws Exception;

	T merge(T parsed, T read, Context context) throws Exception;

	void write(T merged, Context context) throws Exception;

}

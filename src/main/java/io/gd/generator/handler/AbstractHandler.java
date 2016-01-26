package io.gd.generator.handler;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.gd.generator.context.Context;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

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
	
	protected String renderTemplate(String tmplName, Map<String, Object> model, S context) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		StringWriter out = new StringWriter();
		Template template = context.getFreemarkerConfiguration().getTemplate(tmplName + ".ftl");
		template.process(model, out);
		return out.toString();
	}

}

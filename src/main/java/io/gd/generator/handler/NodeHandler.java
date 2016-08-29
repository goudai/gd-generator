package io.gd.generator.handler;

import freemarker.template.Template;
import io.gd.generator.meta.node.Exports;
import io.gd.generator.meta.node.Method;
import io.gd.generator.meta.node.NodeMeta;
import io.gd.generator.meta.node.Service;
import io.gd.generator.util.ClassHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.lang.reflect.Parameter;
import java.util.*;


public class NodeHandler extends AbstractHandler {


	NodeMeta nodeMeta = new NodeMeta();
	Set<Class<?>> beans = new LinkedHashSet<>();

	@Override
	public void doHandle(Set<Class<?>> classes) {
		classes.stream().filter(service ->!(service.getName().endsWith("Impl"))).forEach(service ->{
			nodeMeta.getServices().add(new Service(service.getName(),service.getSimpleName()));
			Exports exports = new Exports(service.getSimpleName());
			nodeMeta.getExports().add(exports);
			Arrays.stream(service.getMethods()).forEach(method -> {
				String name = method.getName();
				final StringBuilder jsonParamters = new StringBuilder("{");
				Parameter[] parameters = method.getParameters();
				if(parameters.length == 0){
					exports.getMethods().add(new Method(method.getName(),"{}"));
				}else{
					Arrays.stream(parameters).forEach(parameter -> {
						Class<?> type = parameter.getType();
						jsonParamters.append(parameter.getName()+":"+getTypeName(type,beans) +",");

					});
					String s = jsonParamters.toString();
					s = s.substring(0,s.length()-1);
					s+="}";
					exports.getMethods().add(new Method(name,s));
				}

			});
		});

		try {
			StringWriter out = new StringWriter();
			Template template = null;
			template = freemarkerConfiguration.getTemplate("node-yield.ftl");
			template.process(new HashMap<String,Object>(){{put("node",nodeMeta);}}, out);

			String doc = "";
			for (Class<?> bean : beans) {
					StringBuilder builder = new StringBuilder(bean.getName() +": {");
					ClassHelper.getFields(bean).forEach(field -> {
						builder.append(field.getName()+ ":" + getTypeName(field.getType(),null)+", ");
					});
				String s = builder.toString();
				s = s.substring(0,s.length()-2);
				s+="}";
				doc +=s + "\n";
			}
			try (FileWriter writer = new FileWriter(new File(config.getNodeDestFile()))){
				writer.write(out.toString());
			}
			try (FileWriter writer = new FileWriter(new File(config.getNodeDocFile()))){
				writer.write(doc.toString());
			}
			System.out.println("success ");
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public  boolean isWrapClass(Class clz) {
		try {
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	String getTypeName(Class<?> type,Set<Class<?>> sets){
		if(type.isPrimitive() || Date.class.isAssignableFrom(type)|| (this.isWrapClass(type)))
			return  type.getSimpleName();
		else if(String.class.isAssignableFrom(type))
			return "''";
		else if (Enum.class.isAssignableFrom(type)){
			Enum[] enumConstants = ((Class<? extends Enum>) type).getEnumConstants();
			String s = "";
			for (Enum enumConstant : enumConstants) {
				s+=enumConstant.name() +"="+enumConstant.ordinal()+",";
			}
			return "enum{"+s.substring(0,s.length()-1)+"}";
		}

		else if(type.isArray())
			return"[]";
		else {
			if(sets != null)
			sets.add(type);
			return type.getName();
		}
	}


}

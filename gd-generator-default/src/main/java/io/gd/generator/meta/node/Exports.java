package io.gd.generator.meta.node;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by freeman on 16/6/21.
 */
public class Exports {
	private  String serviceName;

	private Set<Method> methods = new LinkedHashSet<>();

	public Exports(String serviceName) {
		this.serviceName = serviceName;
	}

	public Set<Method> getMethods() {
		return methods;
	}

	public void setMethods(Set<Method> methods) {
		this.methods = methods;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}

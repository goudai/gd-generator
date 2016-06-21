package io.gd.generator.meta.node;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by freeman on 16/6/21.
 */
public class NodeMeta {
	private Set<Service> services = new LinkedHashSet<>();
	private Set<Exports> exports = new LinkedHashSet<>();


	public Set<Service> getServices() {
		return services;
	}

	public void setServices(Set<Service> services) {
		this.services = services;
	}

	public Set<Exports> getExports() {
		return exports;
	}

	public void setExports(Set<Exports> exports) {
		this.exports = exports;
	}
}

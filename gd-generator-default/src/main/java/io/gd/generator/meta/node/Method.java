package io.gd.generator.meta.node;

/**
 * Created by freeman on 16/6/21.
 */
public class Method {
	private String name;
	private String json;
	private String serviceName;

	public Method(String name, String json) {
		this.json = json;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}

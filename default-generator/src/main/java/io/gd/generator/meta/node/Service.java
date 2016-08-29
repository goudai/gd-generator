package io.gd.generator.meta.node;

/**
 * Created by freeman on 16/6/21.
 */
public class Service {
	private String name;
	private String simpleName;

	public Service(String name, String simpleName) {
		this.name = name;
		this.simpleName = simpleName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
}

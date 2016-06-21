package io.gd.generator.config;

import java.io.File;

/**
 * Created by freeman on 16/6/21.
 */
public class NodeConfig extends Config {
	private File distFile;
	private File docFile;
	private String servicePackage;

	public File getDistFile() {
		return distFile;
	}

	public void setDistFile(File distFile) {
		this.distFile = distFile;
	}

	public File getDocFile() {
		return docFile;
	}

	public void setDocFile(File docFile) {
		this.docFile = docFile;
	}

	public String getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}
}

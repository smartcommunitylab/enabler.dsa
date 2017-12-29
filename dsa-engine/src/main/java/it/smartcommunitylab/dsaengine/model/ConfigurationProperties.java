package it.smartcommunitylab.dsaengine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurationProperties {
	private String indexFormat;
	private String archivePolicy;
	private List<String> clients = new ArrayList<String>();
	private Map<String, Object> dataMapping;
	
	public String getIndexFormat() {
		return indexFormat;
	}
	public void setIndexFormat(String indexFormat) {
		this.indexFormat = indexFormat;
	}
	public String getArchivePolicy() {
		return archivePolicy;
	}
	public void setArchivePolicy(String archivePolicy) {
		this.archivePolicy = archivePolicy;
	}
	public List<String> getClients() {
		return clients;
	}
	public void setClients(List<String> clients) {
		this.clients = clients;
	}
	public Map<String, Object> getDataMapping() {
		return dataMapping;
	}
	public void setDataMapping(Map<String, Object> dataMapping) {
		this.dataMapping = dataMapping;
	}

	
	
}

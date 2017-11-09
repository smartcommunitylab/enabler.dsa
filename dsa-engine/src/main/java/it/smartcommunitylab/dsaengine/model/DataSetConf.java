package it.smartcommunitylab.dsaengine.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataSetConf extends BaseObject {
	private String domain;
	private String dataset;
	private String indexFormat;
	private String archivePolicy;
	private List<String> users = new ArrayList<String>();
	private List<String> clients = new ArrayList<String>();
	private Map<String, Object> dataMapping;
	private Date lastCheck;
	private String elasticUser;
	private String elasticPassword;
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getDataset() {
		return dataset;
	}
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
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
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
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
	
	@Override
	public String toString() {
		return domain + "/" + dataset + "/" + getId();
	}
	public Date getLastCheck() {
		return lastCheck;
	}
	public void setLastCheck(Date lastCheck) {
		this.lastCheck = lastCheck;
	}
	public String getElasticUser() {
		return elasticUser;
	}
	public void setElasticUser(String elasticUser) {
		this.elasticUser = elasticUser;
	}
	public String getElasticPassword() {
		return elasticPassword;
	}
	public void setElasticPassword(String elasticPassword) {
		this.elasticPassword = elasticPassword;
	}
}

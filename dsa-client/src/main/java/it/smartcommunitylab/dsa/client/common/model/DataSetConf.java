package it.smartcommunitylab.dsa.client.common.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSetConf extends BaseObject {
	private String domain;
	private String dataset;
	
	private ConfigurationProperties configurationProperties;
	
	private Date lastCheck;
	private String elasticUser;
	private String elasticPassword;
	
	private List<User> users = new ArrayList<User>();
	
//	private Boolean elasticDomainUser = Boolean.FALSE;
	
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
	
	public ConfigurationProperties getConfigurationProperties() {
		return configurationProperties;
	}
	public void setConfigurationProperties(ConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
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
//	public Boolean getElasticDomainUser() {
//		return elasticDomainUser;
//	}
//	public void setElasticDomainUser(Boolean elasticDomainUser) {
//		this.elasticDomainUser = elasticDomainUser;
//	}
	
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	@Override
	public String toString() {
		return domain + "/" + dataset + "/" + getId();
	}	
	
}

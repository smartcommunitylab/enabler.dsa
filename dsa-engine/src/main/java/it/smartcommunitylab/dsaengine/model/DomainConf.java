package it.smartcommunitylab.dsaengine.model;

import java.util.ArrayList;
import java.util.List;

public class DomainConf extends BaseObject {
	
	private ConfigurationProperties defaultConfigurationProperties;
	
	private String elasticUser;
	private String elasticPassword;	

	private List<Manager> managers = new ArrayList<Manager>();
	private List<User> users = new ArrayList<User>();

	public List<Manager> getManagers() {
		return managers;
	}

	public void setManagers(List<Manager> manager) {
		this.managers = manager;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public ConfigurationProperties getDefaultConfigurationProperties() {
		return defaultConfigurationProperties;
	}

	public void setDefaultConfigurationProperties(ConfigurationProperties defaultConfigurationProperties) {
		this.defaultConfigurationProperties = defaultConfigurationProperties;
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

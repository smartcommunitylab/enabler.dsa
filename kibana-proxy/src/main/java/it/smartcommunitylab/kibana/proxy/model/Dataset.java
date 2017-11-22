package it.smartcommunitylab.kibana.proxy.model;

import java.util.List;

import com.google.common.collect.Lists;

import it.smartcommunitylab.dsa.client.common.model.ExternalUser;

public class Dataset {

	private String name;
	
	private List<ExternalUser> users = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ExternalUser> getUsers() {
		return users;
	}

	public void setUsers(List<ExternalUser> users) {
		this.users = users;
	}
	
	
	
}

package it.smartcommunitylab.dsaengine.model;

import java.util.List;

public class Profile {

	private String username;
	private String displayname;
	private List<ProfileDomainData> domains;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public List<ProfileDomainData> getDomains() {
		return domains;
	}
	public void setDomains(List<ProfileDomainData> domains) {
		this.domains = domains;
	}
	
	
	
}

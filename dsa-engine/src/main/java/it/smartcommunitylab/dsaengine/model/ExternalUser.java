package it.smartcommunitylab.dsaengine.model;

import java.util.ArrayList;
import java.util.List;

public class ExternalUser extends BaseObject {

	private String email;
	private List<DomainRole> domainRoles = new ArrayList<DomainRole>();
	private List<DataSetRole> datasetRoles = new ArrayList<DataSetRole>();

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<DomainRole> getDomainRoles() {
		return domainRoles;
	}

	public void setDomainRoles(List<DomainRole> domainRoles) {
		this.domainRoles = domainRoles;
	}

	public List<DataSetRole> getDatasetRoles() {
		return datasetRoles;
	}

	public void setDatasetRoles(List<DataSetRole> datasetRoles) {
		this.datasetRoles = datasetRoles;
	}
	
}

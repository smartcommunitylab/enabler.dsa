package it.smartcommunitylab.dsaengine.kibana.controller;

public class DomainDataSetInput {

	private String domain;
	private String dataset;
	private String postDashParameters;
	
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
	public String getPostDashParameters() {
		return postDashParameters;
	}
	public void setPostDashParameters(String postDashParameters) {
		this.postDashParameters = postDashParameters;
	}
	
	
	
}

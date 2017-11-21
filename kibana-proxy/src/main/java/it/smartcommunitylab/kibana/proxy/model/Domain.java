package it.smartcommunitylab.kibana.proxy.model;

import java.util.List;

import com.google.common.collect.Lists;

public class Domain {

	private String name;
	
	private List<Dataset> datasets = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}
	
	
	
}

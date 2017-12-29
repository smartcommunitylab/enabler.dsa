package it.smartcommunitylab.dsa.client.common.model;

public class BaseDataSetConf {
	private String dataset;
	
	private ConfigurationProperties configurationProperties;
	
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

	public DataSetConf toDataSetConf() {
		DataSetConf dsConf = new DataSetConf();
		dsConf.setDataset(dataset);
		dsConf.setConfigurationProperties(configurationProperties);
		return dsConf;
	}
	
}

package it.smartcommunitylab.dsaengine.model;

public class BaseDomainConf {

	private ConfigurationProperties defaultConfigurationProperties;

	public ConfigurationProperties getDefaultConfigurationProperties() {
		return defaultConfigurationProperties;
	}

	public void setDefaultConfigurationProperties(ConfigurationProperties defaultConfigurationProperties) {
		this.defaultConfigurationProperties = defaultConfigurationProperties;
	}
	
	public DomainConf toDomainConf() {
		DomainConf dConf = new DomainConf();
		dConf.setDefaultConfigurationProperties(this.getDefaultConfigurationProperties());
		return dConf;
	}
	
}

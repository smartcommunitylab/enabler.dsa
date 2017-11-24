package it.smartcommunitylab.dsaengine.kibana.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.dsaengine.common.Utils;

@Component
public class ControllerUtils {
	
	@Autowired
	@Value("${aac.serverUrl}")
	private String aacUrl;	
	
	@Autowired
	@Value("${server.contextPath}")
	private String contextPath;	
	
	@Autowired
	@Value("${server.port}")
	private String serverPort;		

	public String extractEmailFromAccountProfile(AccountProfile accountProfile) {
		String email = null;
		if (Utils.isNotEmpty(accountProfile.getAttribute("adc", "pat_attribute_email"))) {
			email = accountProfile.getAttribute("adc", "pat_attribute_email");
		} else if (Utils.isNotEmpty(accountProfile.getAttribute("google", "email"))) {
			email = accountProfile.getAttribute("google", "email");
		} else if (Utils.isNotEmpty(accountProfile.getAttribute("facebook", "email"))) {
			email = accountProfile.getAttribute("facebook", "email");
		} else if (Utils.isNotEmpty(accountProfile.getAttribute("internal", "email"))) {
			email = accountProfile.getAttribute("internal", "email");
		}
		return email;
	}	
	
	public String loginUrl() {
		return aacUrl + "/logout?target=http://localhost:" + serverPort + contextPath;
	}	
	
}

package it.smartcommunitylab.dsaengine.controller;

import it.smartcommunitylab.aac.AACException;
import it.smartcommunitylab.aac.AACRoleService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.model.Role;
import it.smartcommunitylab.aac.model.TokenData;
import it.smartcommunitylab.dsaengine.exception.UnauthorizedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	@Value("${aac.clientId}")	
	private String clientId;

	@Autowired
	@Value("${aac.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${aac.serverUrl}")	
	private String aacURL;
	
	private AACRoleService roleService;
	
	private AACService aacService;
	
	private TokenData tokenData = null;

	@PostConstruct
	public void init() throws Exception {
		roleService = new AACRoleService(aacURL);
		aacService = new AACService(aacURL, clientId, clientSecret);
	}
	
	protected String refreshToken() throws UnauthorizedException {
		if((tokenData == null) || isTokenExpired()) {
      try {
      	tokenData = aacService.generateClientToken(null);
	    } catch (Exception e) {
	    	throw new UnauthorizedException("error connectiong to oauth/token:" + e.getMessage());
			}
		}
		return tokenData.getAccess_token();
	}
	
	protected boolean isTokenExpired() {
		boolean result = false;
		if(tokenData == null) {
			return true;
		}
		if(tokenData.getExpires_on() > 0) {
			long now = System.currentTimeMillis();
			if(now > tokenData.getExpires_on()) {
				result = true;
			}
		}
		return result;
	}

	protected List<String> getRoles() throws UnauthorizedException, SecurityException, AACException {
		if(isTokenExpired()) {
			refreshToken();
		}
		Set<Role> roles = roleService.getClientRoles(tokenData.getAccess_token());
		List<String> result = new ArrayList<String>();
		for(Role role : roles) {
			result.add(role.getRole());
		}
		return result;
	}
	
	protected boolean checkRole(String role) throws SecurityException, UnauthorizedException, AACException {
		boolean result = false;
		List<String> roles = getRoles();
		if(roles != null) {
			result = roles.contains(role);
		}
		return result;
	}
	
}

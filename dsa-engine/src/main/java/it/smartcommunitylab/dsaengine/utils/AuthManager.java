package it.smartcommunitylab.dsaengine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.aac.AACException;
import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACRoleService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.model.BasicProfile;
import it.smartcommunitylab.aac.model.Role;
import it.smartcommunitylab.aac.model.TokenData;
import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.exception.UnauthorizedException;

@Component
public class AuthManager {
	private static final transient Logger logger = LoggerFactory.getLogger(AuthManager.class);

	@Autowired
	@Value("${security.oauth2.client.clientId}")	
	private String clientId;

	@Autowired
	@Value("${security.oauth2.client.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${aac.serverUrl}")	
	private String aacURL;
	
	@Autowired
	@Value("${profile.serverUrl}")
	private String profileServerUrl;
	
	private AACRoleService roleService;
	
	private AACService aacService;
	
	private AACProfileService profileConnector;
	
	private TokenData tokenData = null;

	@PostConstruct
	public void init() throws Exception {
		roleService = new AACRoleService(aacURL);
		profileConnector = new AACProfileService(profileServerUrl);
		aacService = new AACService(aacURL, clientId, clientSecret);
	}
	
	public String refreshToken() throws UnauthorizedException {
		if((tokenData == null) || isTokenExpired()) {
      try {
      	tokenData = aacService.generateClientToken(null);
	    } catch (Exception e) {
	    	throw new UnauthorizedException("error connectiong to oauth/token:" + e.getMessage());
			}
		}
		return tokenData.getAccess_token();
	}
	
	public boolean isTokenExpired() {
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

	public List<String> getRoles(String clientToken) 
			throws UnauthorizedException, SecurityException, AACException {
		if(isTokenExpired()) {
			refreshToken();
		}
		Set<Role> roles = roleService.getRolesByToken(tokenData.getAccess_token(), clientToken);
		List<String> result = new ArrayList<String>();
		for(Role role : roles) {
			result.add(role.getRole());
		}
		return result;
	}
	
	public List<String> getUserRoles(String userToken) 
			throws UnauthorizedException, SecurityException, AACException {
		if(isTokenExpired()) {
			refreshToken();
		}
		Set<Role> roles = roleService.getRoles(userToken);
		List<String> result = new ArrayList<String>();
		for(Role role : roles) {
			result.add(role.getRole());
		}
		return result;
	}
	
	public boolean checkRole(String role, HttpServletRequest request) 
			throws SecurityException, UnauthorizedException, AACException {
		boolean result = false;
		String clientToken = getAuthToken(request);
		if(Utils.isNotEmpty(clientToken)) {
			List<String> roles = getRoles(clientToken);
			if(roles != null) {
				result = roles.contains(role);
			}
		}
		return result;
	}
	
	public List<String> getRoleWithPrefix(String prefix, String token) 
			throws SecurityException, UnauthorizedException, AACException {
		Set<Role> roles = roleService.getRoles(token);
		List<String> result = roles.stream().filter(x -> x.getRole().startsWith(prefix)).map(y -> y.getRole()).collect(Collectors.toList());

		return result;
	}	
	
	public boolean checkUserRole(String role, HttpServletRequest request) 
			throws SecurityException, UnauthorizedException, AACException {
		boolean result = false;
		String userToken = getAuthToken(request);
		if(Utils.isNotEmpty(userToken)) {
			List<String> roles = getUserRoles(userToken);
			if(roles != null) {
				result = roles.contains(role);
			}
		}
		return result;
	}
	
	public String getAuthToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if(Utils.isNotEmpty(token)) {
			token = token.replace("Bearer ", "");
		}
		return token;
	}
	
	public String getUsername(HttpServletRequest request) {
		return  getBasicProfile(request).getUsername();
	}
	
	private BasicProfile getBasicProfile(HttpServletRequest request) {
		BasicProfile result = null;
		String token = getAuthToken(request);
		if(Utils.isNotEmpty(token)) {
			try {
				result = profileConnector.findProfile(token);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(String.format("getAccoutProfile[%s]: %s", token, e.getMessage()));
				}
			} 
		}
		return result;
	}

}

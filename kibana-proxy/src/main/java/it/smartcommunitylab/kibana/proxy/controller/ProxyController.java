package it.smartcommunitylab.kibana.proxy.controller;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import it.smartcommunitylab.aac.AACRoleService;
import it.smartcommunitylab.aac.model.Role;
import it.smartcommunitylab.aac.model.Role.ROLE_SCOPE;
import it.smartcommunitylab.dsa.client.common.model.DataSetConf;
import it.smartcommunitylab.dsa.client.service.DSAService;

@Controller
@RequestMapping("/")
public class ProxyController {

	@Autowired
	@Value("${kibana.url}")
	private String kibanaUrl;		
	
	@Autowired
	@Value("${kibana.login}")
	private String kibanaLogin;			
	
	@Autowired
	@Value("${kibana.version}")
	private String kibanaVersion;
	
	@Autowired
	@Value("${aac.url}")
	private String aacUrl;	
	
	@Autowired
	@Value("${dsa.url}")
	private String dsaUrl;	
	
	private AACRoleService roleService;
	private DSAService dsaService;
	
	@PostConstruct
	public void init() {
		roleService = new AACRoleService(aacUrl);
		dsaService = new DSAService(dsaUrl);
	}
	
	@GetMapping("/kibana/{domain}/{dataset}")
	public ResponseEntity<Object> kibana(HttpServletRequest request, HttpServletResponse response, @PathVariable String domain, @PathVariable String dataset) throws Exception {
		String token = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		Set<Role> roles = roleService.getRoles(token);
		
		if (!checkRoles(roles, domain)) {
			throw new UnauthorizedUserException("User is not allowed for domain \"" + domain + "\"");
		}
		
		Map<String, String> user = retrieveUser(domain, dataset, token);
		
		HttpHeaders httpHeaders = kibanaLogin(user);
		
		URI uri = new URI(kibanaUrl);
	    httpHeaders.setLocation(uri);
	    return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);	
	}
	
	private boolean checkRoles(Set<Role> roles, String domain) {
		final String elastic = "dsa_" + domain;
		return roles.stream().filter(x -> ROLE_SCOPE.application.equals(x.getScope()) && elastic.equals(x.getRole())).findFirst().isPresent();
	}
	
	private Map<String, String> retrieveUser(String domain, String dataset, String token) throws Exception {
		DataSetConf dsConf = dsaService.getUserDataSetConf(domain, dataset, token);

		String user = dsConf.getElasticUser();
		String password = dsConf.getElasticPassword();
		
		Map<String, String> userMap = new TreeMap<String, String>();
		userMap.put("username", user);
		userMap.put("password", password);
		
		return userMap;
	}	
	
	private HttpHeaders kibanaLogin(Map<String, String> user) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> res = restTemplate.exchange(kibanaLogin, HttpMethod.POST, new HttpEntity<Object>(user, createHeaders()),
				String.class);
		String data = res.getBody();
		
		HttpHeaders loginHeaders = res.getHeaders();
		System.err.println(loginHeaders);
		
		HttpHeaders resultHeaders = new HttpHeaders();
		resultHeaders.put("kbn-xpack-sig", loginHeaders.get("kbn-xpack-sig"));
		resultHeaders.put("set-cookie", loginHeaders.get("set-cookie"));
		resultHeaders.put("kbn-version", loginHeaders.get("kbn-version"));
		
		return resultHeaders;
	}
	
	HttpHeaders createHeaders() {
		return new HttpHeaders() {
			{
				set("kbn-version", kibanaVersion);
			}
		};
	}	
	
}

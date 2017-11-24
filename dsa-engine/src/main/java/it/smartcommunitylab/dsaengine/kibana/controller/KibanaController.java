package it.smartcommunitylab.dsaengine.kibana.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACRoleService;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.aac.model.Role;
import it.smartcommunitylab.aac.model.Role.ROLE_SCOPE;
import it.smartcommunitylab.dsaengine.kibana.utils.ControllerUtils;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.model.ExternalUser;
import it.smartcommunitylab.dsaengine.storage.ExternalUserRepository;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;

@Controller
@RequestMapping("/")
public class KibanaController {

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
	@Value("${aac.serverUrl}")
	private String aacUrl;	
	
	@Autowired
	private ControllerUtils utils;
	
	@Autowired
	private RepositoryManager dataManager;

	@Autowired
	private ExternalUserRepository userRepository;

	private AACRoleService roleService;
	private AACProfileService profileService;

	@PostConstruct
	public void init() {
		roleService = new AACRoleService(aacUrl);
		profileService = new AACProfileService(aacUrl);
	}

	@GetMapping("/kibanalogin")
	public String kibanalogin(Model model) {
		return "kibanalogin";
	}
	
	@GetMapping("/kibanalogout")
	public String consolelogout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}			
		
		return "kibanalogin";
	}		

	@GetMapping("/kibana")
	public ResponseEntity<Object> kibana(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam String domain, @RequestParam String dataset, Model model)	
			throws Exception {
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AccountProfile profile = profileService.findAccountProfile(token);
		String email = utils.extractEmailFromAccountProfile(profile);

		if (email == null) {
			throw new UnauthorizedUserException("User email is null");
		}

		Set<Role> roles = roleService.getRoles(token);

		if (!checkRoles(roles, domain)) {
			throw new UnauthorizedUserException("User \"" + email + "\" is not allowed for domain \"" + domain + "\"");
		}
		if (!isAllowed(domain, dataset, email)) {
			throw new UnauthorizedUserException("User \"" + email + "\" is not allowed for domain \"" + domain + "\", dataset \"" + dataset + "\"");
		}

		Map<String, String> user = retrieveUser(domain, dataset, email);

		HttpHeaders httpHeaders = kibanaLogin(user);
		URI uri = new URI(kibanaUrl);
		httpHeaders.setLocation(uri);

		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	private boolean checkRoles(Set<Role> roles, String domain) {
		final String elastic = "dsa_" + domain;
		return roles.stream().filter(x -> ROLE_SCOPE.application.equals(x.getScope()) && elastic.equals(x.getRole())).findFirst().isPresent();
	}

	private boolean isAllowed(String domain, String dataset, String email) throws Exception {
		List<ExternalUser> users = userRepository.findByDataset(domain, dataset);

		return users.stream().filter(x -> email.equals(x.getEmail())).findFirst().isPresent();
	}

	private Map<String, String> retrieveUser(String domain, String dataset, String email) throws Exception {
		DataSetConf dsConf = dataManager.getDataSetConf(domain, dataset);

		String user = dsConf.getElasticUser();
		String password = dsConf.getElasticPassword();

		Map<String, String> userMap = new TreeMap<String, String>();
		userMap.put("username", user);
		userMap.put("password", password);

		return userMap;
	}

	private HttpHeaders kibanaLogin(Map<String, String> user) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> res = restTemplate.exchange(kibanaLogin, HttpMethod.POST, new HttpEntity<Object>(user, createHeaders()), String.class);
//		String data = res.getBody();

		HttpHeaders loginHeaders = res.getHeaders();

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

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String exception(HttpServletRequest request, HttpServletResponse response, final Throwable throwable, final Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
		model.addAttribute("error", errorMessage);
		model.addAttribute("login", utils.loginUrl() + "/kibanalogin");
		
		return "kibanaerror";
	}

}

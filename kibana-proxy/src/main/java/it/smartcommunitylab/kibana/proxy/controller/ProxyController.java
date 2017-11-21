package it.smartcommunitylab.kibana.proxy.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACRoleService;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.aac.model.Role;
import it.smartcommunitylab.aac.model.Role.ROLE_SCOPE;
import it.smartcommunitylab.dsa.client.common.Utils;
import it.smartcommunitylab.dsa.client.common.model.DataSetConf;
import it.smartcommunitylab.dsa.client.common.model.ExternalUser;
import it.smartcommunitylab.dsa.client.service.DSAService;
import it.smartcommunitylab.kibana.proxy.model.Dataset;
import it.smartcommunitylab.kibana.proxy.model.Domain;

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
	private AACProfileService profileService;
	private DSAService dsaService;

	@PostConstruct
	public void init() {
		roleService = new AACRoleService(aacUrl);
		profileService = new AACProfileService(aacUrl);
		dsaService = new DSAService(dsaUrl);
	}

	@GetMapping("/console/{domain}")
	public String console(HttpServletRequest request, HttpServletResponse response, HttpSession session, @PathVariable String domain, Model model) {
		try {
			model.addAttribute("logout", aacUrl + "/logout");
			model.addAttribute("login", request.getRequestURL().toString());			
			
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AccountProfile profile = profileService.findAccountProfile(token);
		String email = extractEmailFromAccountProfile(profile);
		
		if (email == null) {
			throw new UnauthorizedUserException("User email is null");
		}
		
		Set<Role> roles = roleService.getRoles(token);

		if (!checkRoles(roles, domain)) {
			throw new UnauthorizedUserException("User is not allowed for domain \"" + domain + "\"");
		}		

		Domain dom = retrieveDataSets(domain, email, token);
		ObjectMapper mapper = new ObjectMapper();
		System.err.println(mapper.writeValueAsString(dom));		
		
		
		model.addAttribute("user", email);
		model.addAttribute("domain", domain);
		model.addAttribute("dscs", dom);
		
		return "/console";
		} catch (Exception e) {
			model.addAttribute("message", "???");
			model.addAttribute("error", e.getMessage());
			return "/console";
//			return consoleLogout(request, response, model, request.getRequestURL().toString());
		}
	}
	
	@PostMapping("/console")
	public String consolePost(@ModelAttribute TreeMap<String, List<String>> dscs, Model model) {
		System.err.println(dscs);
		return "/console";
	}
	
	@GetMapping("/domain/info/{domain}")
	public ResponseEntity<Object> info(@PathVariable String domain, Model model) throws Exception {
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AccountProfile profile = profileService.findAccountProfile(token);
		String email = extractEmailFromAccountProfile(profile);
		
		Domain dom = retrieveDataSets(domain, email, token);
		
		return new ResponseEntity<Object>(dom, HttpStatus.OK);
	}
	
	@PostMapping("/adduser/{domain}/{dataset}")
	public ResponseEntity<Object> addUser(@PathVariable String domain, @PathVariable String dataset, @RequestBody List<ExternalUser> users) throws Exception {
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AccountProfile profile = profileService.findAccountProfile(token);
		String email = extractEmailFromAccountProfile(profile);
		
		dsaService.setDataSetConfUsers(domain, dataset, users, token);
		
		Domain dom = retrieveDataSets(domain, email, token);
		
		return new ResponseEntity<Object>(dom, HttpStatus.OK);
	}	
	
	
	@GetMapping("/kibana/{domain}/{dataset}")
	public ResponseEntity<Object> kibana(HttpServletRequest request, HttpServletResponse response, HttpSession session, @PathVariable String domain, @PathVariable String dataset, Model model) {
		try {
			String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			AccountProfile profile = profileService.findAccountProfile(token);
			String email = extractEmailFromAccountProfile(profile);
			
			if (email == null) {
				throw new UnauthorizedUserException("User email is null");
			}
			
			Set<Role> roles = roleService.getRoles(token);

			if (!checkRoles(roles, domain)) {
				throw new UnauthorizedUserException("User is not allowed for domain \"" + domain + "\"");
			}

			Map<String, String> user = retrieveUser(domain, dataset, email, token);

			HttpHeaders httpHeaders = kibanaLogin(user);
			URI uri = new URI(kibanaUrl);
			httpHeaders.setLocation(uri);

			return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
		} catch (Exception e) {
//			return logout(request, response, e);
//			model.addAttribute("message", "???");
//			model.addAttribute("error", e.getMessage());
//			model.addAttribute("login", aacUrl + "/logout?target=" + request.getRequestURL().toString());
//			model.addAttribute("logout",  aacUrl + "/logout");			
			return kibanaLogout(request, response, e);			
		}
	}	
	
	
	@GetMapping("/consolelogout")
	private String consoleLogout(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam String login) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				cookies[i].setValue("");
				cookies[i].setPath("/");
				cookies[i].setMaxAge(0);
				response.addCookie(cookies[i]);
			}
		}		
		
		String redirect = "redirect:" + aacUrl + "/logout?target=" + login;
		System.err.println(redirect);
		return redirect;
	}		
	
	@GetMapping("/kibanalogout")
	private String logout(HttpServletRequest request, Model model, @RequestParam String login, @RequestParam String logout) {
		model.addAttribute("message", request.getHeader("message"));
		model.addAttribute("error", request.getHeader("error"));
		model.addAttribute("login", login);
		model.addAttribute("logout", logout);
		System.err.println(request.getRequestURL());
		return "kibanalogin";
	}	
	
	private ResponseEntity<Object> kibanaLogout(HttpServletRequest request, HttpServletResponse response, Exception e) {
		URI uri = null;
		try {
			uri = new URI("/kibanalogout");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				cookies[i].setValue("");
				cookies[i].setPath("/");
				cookies[i].setMaxAge(0);
				response.addCookie(cookies[i]);
			}
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		if (e != null) {
			httpHeaders.set("error", e.getMessage());
		}
		httpHeaders.set("logout", aacUrl + "/logout");
		httpHeaders.set("login", request.getRequestURL().toString());

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> res = restTemplate.exchange("http://localhost:5555/kibanalogout?login=" + request.getRequestURL() + "&logout=" + aacUrl + "/logout", HttpMethod.GET,
				new HttpEntity<Object>(null, httpHeaders), String.class);
//		ResponseEntity<String> res = restTemplate.exchange(aacUrl + "/logout" + "?target=" + request.getRequestURL().toString(), HttpMethod.GET,
//				new HttpEntity<Object>(null, httpHeaders), String.class);		
		String data = res.getBody();

		return new ResponseEntity<>(data, HttpStatus.SEE_OTHER);		
	}

	

	private boolean checkRoles(Set<Role> roles, String domain) {
		final String elastic = "dsa_" + domain;
		return roles.stream().filter(x -> ROLE_SCOPE.application.equals(x.getScope()) && elastic.equals(x.getRole())).findFirst().isPresent();
	}

	// private void logout() {
	// RestTemplate restTemplate = new RestTemplate();
	// ResponseEntity<String> res = restTemplate.exchange(aacUrl + "/logout", HttpMethod.POST, new HttpEntity<Object>(null),
	// String.class);
	// String data = res.getBody();
	// System.err.println(data);
	// }

	// private void logout(String token) {
	// RestTemplate restTemplate = new RestTemplate();
	// ResponseEntity<String> res = restTemplate.exchange(aacUrl + "/logout", HttpMethod.GET, new HttpEntity<Object>(null), String.class);
	// String data = res.getBody();
	// System.err.println(data);
	// }

	private Map<String, String> retrieveUser(String domain, String dataset, String email, String token) throws Exception {
		DataSetConf dsConf = dsaService.getUserDataSetConf(domain, dataset, token);
		
		List<ExternalUser> users = dsConf.getUsers();
		if (!users.stream().filter(x -> email.equals(x.getEmail())).findFirst().isPresent()) {
			throw new UnauthorizedUserException("User is not allowed for domain \"" + domain + "\", dataset \"" + dataset +"\"");
		}

		String user = dsConf.getElasticUser();
		String password = dsConf.getElasticPassword();

		Map<String, String> userMap = new TreeMap<String, String>();
		userMap.put("username", user);
		userMap.put("password", password);

		return userMap;
	}
	
	private Domain retrieveDataSets(String domain, String email, String token) throws Exception {
		List<DataSetConf> dsConfs = dsaService.getDataSetConf(domain, token);
		
		boolean isAll = dsConfs.stream().filter(x -> "all".equals(x.getDataset()) && 
			x.getUsers().stream().filter(y -> 
				email.equals(y.getEmail())
				).findFirst().isPresent()).findFirst().isPresent();
		if (!isAll) {
			throw new UnauthorizedUserException("User is not allowed for domain \"" + domain);
		}		
		
		Domain dom = new Domain();
		dom.setName(domain);
		for (DataSetConf dsConf: dsConfs) {
			Dataset ds = new Dataset();
			ds.setName(dsConf.getDataset());
			ds.setUsers(dsConf.getUsers());
			dom.getDatasets().add(ds);
		}
		
		return dom;
	}

	private String extractEmailFromAccountProfile(AccountProfile accountProfile) {
		String email = null;
		if(Utils.isNotEmpty(
				accountProfile.getAttribute("adc", "pat_attribute_email"))) {
			email = accountProfile.getAttribute("adc", "pat_attribute_email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("google", "email"))) {
			email = accountProfile.getAttribute("google", "email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("facebook", "email"))) {
			email = accountProfile.getAttribute("facebook", "email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("internal", "email"))) {
			email = accountProfile.getAttribute("internal", "email");
		}
		return email;
	}
	
	private HttpHeaders kibanaLogin(Map<String, String> user) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> res = restTemplate.exchange(kibanaLogin, HttpMethod.POST, new HttpEntity<Object>(user, createHeaders()), String.class);
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

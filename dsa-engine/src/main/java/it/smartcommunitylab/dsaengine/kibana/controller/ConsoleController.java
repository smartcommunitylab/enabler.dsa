package it.smartcommunitylab.dsaengine.kibana.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.dsaengine.kibana.utils.ControllerUtils;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.storage.ACLManager;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;

@Controller
public class ConsoleController {

	@Autowired
	@Value("${aac.serverUrl}")
	private String aacUrl;

	@Autowired
	private ControllerUtils utils;
	
	@Autowired
	private RepositoryManager dataManager;
	
//	@Autowired
//	private ExternalUserRepository userRepository;

	@Autowired
	private ACLManager aclManager;	
	
//	private AACRoleService roleService;
	private AACProfileService profileService;

	@PostConstruct
	public void init() {
//		roleService = new AACRoleService(aacUrl);
		profileService = new AACProfileService(aacUrl);
	}

	@GetMapping("/consolelogin")
	public String consolelogin(Model model) {
		return "consolelogin";
	}	
	
	@GetMapping("/consolelogout")
	public String consolelogout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}			
		
		return "consolelogin";
	}	
	
	@GetMapping("console")
	public String console(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam String domain, Model model) throws Exception {	
		model.addAttribute("logout", aacUrl + "/logout");
		model.addAttribute("login", utils.loginUrl() + "/consolelogout");

		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AccountProfile profile = profileService.findAccountProfile(token);
		String email = utils.extractEmailFromAccountProfile(profile);

		if (email == null) {
			throw new UnauthorizedUserException("User email is null");
		}

//		Set<Role> roles = roleService.getRoles(token);
//
//		if (!checkRoles(roles, domain)) {
//			throw new UnauthorizedUserException("User \"" + email + "\" is not allowed for domain \"" + domain + "\"");
//		}
//		if (!isDomainManager(domain, email)) {
//			throw new UnauthorizedUserException("User \"" + email + "\" is not a manager for domain \"" + domain + "\"");
//		}

		if (!aclManager.isDomainManager(domain, email)) {
			throw new UnauthorizedUserException("User \"" + email + "\" is not a manager for domain \"" + domain + "\"");
		}		
		
		List<DataSetConf> ds = retrieveDataSets(domain, email);

		model.addAttribute("user", email);
		model.addAttribute("domain", domain);
		model.addAttribute("dscs", ds);

		return "consolepage";
	}

//	private boolean checkRoles(Set<Role> roles, String domain) {
//		final String elastic = "dsa_" + domain;
//		return roles.stream().filter(x -> ROLE_SCOPE.application.equals(x.getScope()) && elastic.equals(x.getRole())).findFirst().isPresent();
//	}
//
//	private boolean isDomainManager(String domain, String email) {
//		List<ExternalUser> users = userRepository.findByDomain(domain);
//		
//		return users.stream().filter(x -> email.equals(x.getEmail()) && x.getDomainRoles().stream()
//				.filter(y -> domain.equals(y.getDomain()) && y.getRole().ordinal() <= UserRole.valueOf("DOMAIN_MANAGER").ordinal()).findFirst().isPresent()).findFirst().isPresent();
//	}
	
	private List<DataSetConf> retrieveDataSets(String domain, String email) throws Exception {
		List<DataSetConf> dsConfs = dataManager.getDataSetConf(domain);

		dsConfs.forEach(x -> {
			x.setElasticPassword(null);
		});

		return dsConfs;
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String exception(HttpServletRequest request, HttpServletResponse response, final Throwable throwable, final Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}		
		
		String errorMessage = (throwable != null ? (throwable.getClass() + ": " + throwable.getMessage()) : "Unknown error");
		model.addAttribute("error", errorMessage);
		model.addAttribute("login", utils.loginUrl() + "/consolelogin");
		return "consoleerror";
	}
	
}

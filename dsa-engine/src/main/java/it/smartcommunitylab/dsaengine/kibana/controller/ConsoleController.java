package it.smartcommunitylab.dsaengine.kibana.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.aac.model.BasicProfile;
import it.smartcommunitylab.dsaengine.controller.ManagementController;
import it.smartcommunitylab.dsaengine.kibana.utils.ControllerUtils;
import it.smartcommunitylab.dsaengine.kibana.utils.RolesUtils;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.model.DomainConf;
import it.smartcommunitylab.dsaengine.model.Manager;
import it.smartcommunitylab.dsaengine.storage.DomainConfRepository;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;
import it.smartcommunitylab.dsaengine.utils.AuthManager;

@Controller
public class ConsoleController {

	@Autowired
	@Value("${aac.serverUrl}")
	private String aacUrl;

	@Autowired
	private ControllerUtils utils;
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private DomainConfRepository domainRepository;

	@Autowired
	private RepositoryManager repositoryManager;	
	
	@Autowired
	private RolesUtils aclManager;	
	
	@Autowired
	private AuthManager authManager;
	
	private AACProfileService profileService;

	@PostConstruct
	public void init() {
		profileService = new AACProfileService(aacUrl);
	}

	@GetMapping("/consolelogin")
	public String consolelogin(Model model) throws Exception {	
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		BasicProfile profile = profileService.findProfile(token);
		String username = profile.getUsername();
		
		List<String> domainNames = authManager.getRoleWithPrefix(ManagementController.DSA_PROVIDER_ROLE_PREFIX, token);

		for (String domainName: domainNames) {
			DomainConf dConf = repositoryManager.addDomainConf(domainName.replace(ManagementController.DSA_PROVIDER_ROLE_PREFIX, ""));
			
			if (dConf != null) {
			Manager owner = new Manager();
			owner.setId(UUID.randomUUID().toString());
			owner.setUsername(username);
			owner.setOwner(true);
			dConf.getManagers().add(owner);
			
			repositoryManager.updateDomainConfManagers(dConf);
			}
		}
		
		List<DomainConf> domains = domainRepository.findByManager(username);
		List<String> domainIds = domains.stream().map(x -> x.getId()).collect(Collectors.toList());
		
		model.addAttribute("selectedDomain", new DomainDataSetInput());
		model.addAttribute("domains", domainIds);
		model.addAttribute("login", utils.loginUrl() + "/consolelogout");
		
		return "consolelogin";
	}	
	
	@GetMapping("/consolelogout")
	public String consolelogout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			SecurityContextLogoutHandler lh = new SecurityContextLogoutHandler();
			lh.setClearAuthentication(true);
			lh.setInvalidateHttpSession(true);
			lh.logout(request, response, auth);
		}			
		
		return "redirect:consolelogin";
	}	
	
	@PostMapping("console")
	public String console(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam String domain, Model model) throws Exception {
		model.addAttribute("logout", aacUrl + "/logout");
		model.addAttribute("login", utils.loginUrl() + "/consolelogout");
		
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AccountProfile profile = profileService.findAccountProfile(token);
		String email = utils.extractEmailFromAccountProfile(profile);

		if (email == null) {
			throw new UnauthorizedUserException("User email is null");
		}

		if (!aclManager.isDomainManager(domain, email)) {
			throw new UnauthorizedUserException("User \"" + email + "\" is not a manager for domain \"" + domain + "\"");
		}		
		
		List<DataSetConf> ds = retrieveDataSets(domain, email);

		model.addAttribute("user", email);
		model.addAttribute("domain", domain);
		model.addAttribute("dscs", ds);

		return "consolepage";
	}

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
			SecurityContextLogoutHandler lh = new SecurityContextLogoutHandler();
			lh.setClearAuthentication(true);
			lh.setInvalidateHttpSession(true);
			lh.logout(request, response, auth);
		}			
		
		String errorMessage = (throwable != null ? (throwable.getClass() + ": " + throwable.getMessage()) : "Unknown error");
		model.addAttribute("error", errorMessage);
		model.addAttribute("login", utils.loginUrl() + "/consolelogin");
		return "consoleerror";
	}
	
}

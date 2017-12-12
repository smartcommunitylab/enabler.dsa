package it.smartcommunitylab.dsaengine.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.ApiImplicitParam;
import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.elastic.ElasticManger;
import it.smartcommunitylab.dsaengine.exception.BadRequestException;
import it.smartcommunitylab.dsaengine.exception.EntityNotFoundException;
import it.smartcommunitylab.dsaengine.exception.StorageException;
import it.smartcommunitylab.dsaengine.exception.UnauthorizedException;
import it.smartcommunitylab.dsaengine.model.BaseDataSetConf;
import it.smartcommunitylab.dsaengine.model.BaseDomainConf;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.model.DomainConf;
import it.smartcommunitylab.dsaengine.model.Manager;
import it.smartcommunitylab.dsaengine.model.User;
import it.smartcommunitylab.dsaengine.storage.ACLManager;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;
import it.smartcommunitylab.dsaengine.utils.AuthManager;

@Controller
public class ManagementController {
	public static final String DSA_PROVIDER_ROLE_PREFIX = "DSA_PROVIDER_";

	private static final transient Logger logger = LoggerFactory.getLogger(ManagementController.class);
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private ElasticManger elasticManager;
	
	@Autowired
	private ACLManager aclManager;
	
	@Autowired
	private AuthManager authManager;
	
	//////////////////
	// Dataset CRUD //
	//////////////////
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/datasets/{dataset}", method = RequestMethod.GET)
	public @ResponseBody DataSetConf geteDataSetConf (
			@PathVariable String domain,
			@PathVariable String dataset,
			HttpServletRequest request) throws Exception {
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isDatasetManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		

		DataSetConf result = dataManager.getDataSetConf(domain, dataset);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDataSetConf: %s", result.toString()));
		}
		
		if (result != null) {
			result.setElasticPassword(null);
		}
		
		return result;
	}	
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/datasets", method = RequestMethod.GET)
	public @ResponseBody List<DataSetConf> geteDataSetConfs (
			@PathVariable String domain,
			HttpServletRequest request) throws Exception {
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isDatasetManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		

		List<DataSetConf> result = dataManager.getDataSetConf(domain);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDataSetConf: %s", result.toString()));
		}
		
		if (result != null) {
			result.stream().forEach(x -> x.setElasticPassword(null));
		}		
		
		return result;
	}	
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/datasets", method = RequestMethod.POST)
	public @ResponseBody DataSetConf addDataSetConf (
			@PathVariable String domain,
			@RequestBody BaseDataSetConf baseConf,
			HttpServletRequest request) throws Exception {
	
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isDatasetManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}

		if (baseConf.getDataset() == null) {
			throw new BadRequestException("Missing dataset");
		}					
		
		DataSetConf conf = baseConf.toDataSetConf();
		
		conf.setDomain(domain);
		
		DomainConf parentDomain = dataManager.getDomainConf(domain);
		if (parentDomain == null) {
			throw new StorageException("Domain does not exist");
		}
		
		if (conf.getConfigurationProperties() == null) {
			conf.setConfigurationProperties(parentDomain.getDefaultConfigurationProperties());
		}
		
		DataSetConf result = dataManager.addDataSetConf(conf);

		elasticManager.addIndex(result);
		elasticManager.addRole(result);
		elasticManager.addUser(result);
		
//		DataSetConf dataSetConf = dataManager.getDataSetConf(domain, Const.DOMAIN_DATASET);
//		if(dataSetConf == null) {
//			dataSetConf = new DataSetConf();
//			dataSetConf.setDomain(domain);
//			dataSetConf.setDataset(Const.DOMAIN_DATASET);
//			dataSetConf.setElasticDomainUser(true);
//			dataManager.addDataSetConf(dataSetConf);


//		}		
		

		if(logger.isInfoEnabled()) {
			logger.info(String.format("addDataSetConf: %s", result.toString()));
		}
		
		result.setElasticPassword(null);
		return result;
	}
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/datasets/{datasetId}", method = RequestMethod.PUT)
	public @ResponseBody DataSetConf updateDataSetConf (
			@PathVariable String domain,
			@PathVariable String datasetId,
			@RequestBody BaseDataSetConf baseConf,
			HttpServletRequest request) throws Exception {
//		if(!checkRole("dsa_" + domain.toLowerCase(), request)) {
//			throw new UnauthorizedException("Unauthorized Exception: role not valid");
//		}
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isDatasetManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		

		DataSetConf result = dataManager.updateDataSetConf(baseConf, domain, datasetId);
		elasticManager.addIndex(result);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateDataSetConf: %s", result.toString()));
		}
		
		result.setElasticPassword(null);
		return result;
	}
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/datasets/{datasetId}", method = RequestMethod.DELETE)
	public @ResponseBody DataSetConf deleteDataSetConf (
			@PathVariable String domain,
			@PathVariable String datasetId,
			HttpServletRequest request) throws Exception {
//		if(!checkRole("dsa_" + domain.toLowerCase(), request)) {
//			throw new UnauthorizedException("Unauthorized Exception: role not valid");
//		}
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isDatasetManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		
		
		DataSetConf result = dataManager.removeDataSetConfById(domain, datasetId);
		elasticManager.deleteIndex(domain, result.getDataset());
		elasticManager.deleteUser(result);
		elasticManager.deleteRole(result);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteDataSetConf: %s", result.toString()));
		}
		
		result.setElasticPassword(null);		
		return result;
	}
	
	
	/////////////////
	// Domain CRUD //
	/////////////////

	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/domains", method = RequestMethod.GET)
	public @ResponseBody DomainConf getDomainConfs(@PathVariable String domain, HttpServletRequest request) throws Exception {
		if (!authManager.checkRole(DSA_PROVIDER_ROLE_PREFIX + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}

		DomainConf result = dataManager.getDomainConf(domain);

		if (logger.isInfoEnabled()) {
			logger.info(String.format("getDomainConf: %s", result.toString()));
		}
		
		if (result != null) {
			result.setElasticPassword(null);
		}
		return result;
	}	

	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/domains", method = RequestMethod.POST)
	public @ResponseBody DomainConf addDomainConf(@PathVariable String domain, @RequestBody BaseDomainConf baseConf, HttpServletRequest request) throws Exception {
		if (!authManager.checkRole(DSA_PROVIDER_ROLE_PREFIX + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}

		String email = authManager.getEmail(request);

		DomainConf conf = baseConf.toDomainConf();
		conf.setDomain(domain);
		
		Manager owner = new Manager();
		owner.setId(UUID.randomUUID().toString());
		owner.setEmail(email);
		owner.setOwner(true);
		
		conf.getManagers().add(owner);
		
		DomainConf result = dataManager.addDomainConf(conf);
		
		elasticManager.addDomainRole(result);
		elasticManager.addDomainUser(result);		

		if (logger.isInfoEnabled()) {
			logger.info(String.format("addDomainConf: %s", result.toString()));
		}
		
		result.setElasticPassword(null);		
		return result;
	}
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/domains", method = RequestMethod.PUT)
	public @ResponseBody DomainConf updateDomainConf (
			@PathVariable String domain,
			@RequestBody BaseDomainConf baseConf,
			HttpServletRequest request) throws Exception {
		if (!authManager.checkRole(DSA_PROVIDER_ROLE_PREFIX + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		
		DomainConf result = dataManager.updateDomainConf(baseConf, domain);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateDomainConf: %s", result.toString()));
		}
		
		result.setElasticPassword(null);		
		return result;
	}
	
	// TODO delete by id?
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/domains", method = RequestMethod.DELETE)
	public @ResponseBody DomainConf deleteDomainConf (
			@PathVariable String domain,
			HttpServletRequest request) throws Exception {
		if (!authManager.checkRole(DSA_PROVIDER_ROLE_PREFIX + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}		
		
		DomainConf result = dataManager.removeDomainConf(domain);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteDomainConf: %s", result.toString()));
		}
		
		result.setElasticPassword(null);		
		return result;
	}	
	
	//////////////////
	// Manager CRUD //
	//////////////////		
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/managers", method = RequestMethod.GET)
	public @ResponseBody List<Manager> getManagers (
			@PathVariable String domain,
			HttpServletRequest request) throws Exception {
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		
		
		DomainConf dom = dataManager.getDomainConf(domain);
		if(dom == null) {
			throw new EntityNotFoundException("Domain not found");
		}		
		return dom.getManagers();
	}	
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/managers/{managerId}", method = RequestMethod.GET)
	public @ResponseBody Manager getManager (
			@PathVariable String domain,
			@PathVariable String managerId,
			HttpServletRequest request) throws Exception {
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		
		
		DomainConf dom = dataManager.getDomainConf(domain);
		if(dom == null) {
			throw new EntityNotFoundException("Domain not found");
		}		
		
		Optional<Manager> man = dom.getManagers().stream().filter(x -> x.getId().equals(managerId)).findFirst();
		if (!man.isPresent()) {
			throw new BadRequestException("Manager not found");
		}
		return man.get();
	}	
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/managers", method = RequestMethod.POST)
	public @ResponseBody Manager addManager (
			@PathVariable String domain,
			@RequestBody Manager manager,
			HttpServletRequest request) throws Exception {

		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}

		if (manager.getEmail() == null) {
			throw new BadRequestException("Missing email");
		}	

		manager.setId(UUID.randomUUID().toString());
		
		DomainConf dConf = dataManager.getDomainConf(domain);
		if (dConf == null) {
			throw new EntityNotFoundException("Domain not found");
		}
		
		if (dConf.getManagers().contains(manager)) {
			throw new UnauthorizedException("Manager already exist");
		}
		
		dConf.getManagers().add(manager);
		dataManager.updateDomainConfManagers(dConf);
		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addManager: %s", manager.toString()));
		}		
		
		return manager;
	}
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/managers/{managerId}", method = RequestMethod.DELETE)
	public @ResponseBody DomainConf deleteManager (
			@PathVariable String domain,
			@PathVariable String managerId,
			HttpServletRequest request) throws Exception {
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		
		
		DomainConf dom = dataManager.getDomainConf(domain);
		Optional<Manager> man = dom.getManagers().stream().filter(x -> x.getId().equals(managerId)).findFirst();
		if (!man.isPresent()) {
			throw new BadRequestException("Manager not found");
		}
		dom.getManagers().remove(man.get());
		
		DomainConf result = dataManager.updateDomainConfManagers(dom);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteManager: %s", result.toString()));
		}
		return result;
	}		
	
	

	///////////////
	// User CRUD //
	///////////////		
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/users", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsers (
			@PathVariable String domain,
			HttpServletRequest request) throws Exception {
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		
		
		DomainConf dom = dataManager.getDomainConf(domain);
		if(dom == null) {
			throw new EntityNotFoundException("Domain not found");
		}		
		return dom.getUsers();
	}	
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/users/{userId}", method = RequestMethod.GET)
	public @ResponseBody User getUser (
			@PathVariable String domain,
			@PathVariable String userId,
			HttpServletRequest request) throws Exception {
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		
		
		DomainConf dom = dataManager.getDomainConf(domain);
		if(dom == null) {
			throw new EntityNotFoundException("Domain not found");
		}		
		
		Optional<User> usr = dom.getUsers().stream().filter(x -> x.getId().equals(userId)).findFirst();
		if (!usr.isPresent()) {
			throw new BadRequestException("User not found");
		}
		return usr.get();
	}	
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/users", method = RequestMethod.POST)
	public @ResponseBody User addUser (
			@PathVariable String domain,
			@RequestBody User user,
			HttpServletRequest request) throws Exception {

		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}

		if (user.getEmail() == null) {
			throw new BadRequestException("Missing email");
		}	
		user.setId(UUID.randomUUID().toString());
		
		DomainConf dConf = dataManager.getDomainConf(domain);
		if (dConf == null) {
			throw new EntityNotFoundException("Domain not found");
		}
		
		if (dConf.getUsers().contains(user)) {
			throw new UnauthorizedException("User already exist");
		}
		
		dConf.getUsers().add(user);
		dataManager.updateDomainConfUsers(dConf);
		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addUser: %s", user.toString()));
		}		
		
		return user;
	}
	
	@ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, dataType = "string", paramType = "header")
	@RequestMapping(value = "/management/{domain}/users/{userId}", method = RequestMethod.DELETE)
	public @ResponseBody DomainConf deleteUser (
			@PathVariable String domain,
			@PathVariable String userId,
			HttpServletRequest request) throws Exception {
		
		String email = authManager.getEmail(request);
		if(Utils.isNotEmpty(email)) {
			if (!aclManager.isUserManagementAllowed(domain, email)) {
				throw new UnauthorizedException("Unauthorized Exception: role not valid");
			}		
		}		
		
		DomainConf dom = dataManager.getDomainConf(domain);
		Optional<User> man = dom.getUsers().stream().filter(x -> x.getId().equals(userId)).findFirst();
		if (!man.isPresent()) {
			throw new BadRequestException("User not found");
		}
		dom.getUsers().remove(man.get());
		
		DomainConf result = dataManager.updateDomainConfUsers(dom);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteUser: %s", result.toString()));
		}
		return result;
	}	
	
	
	
//	@RequestMapping(value = "/management/user/{domain}", method = RequestMethod.POST)
//	public @ResponseBody User addUser (
//			@PathVariable String domain,
//			@RequestBody User user,
//			HttpServletRequest request) throws Exception {
//
//		String email = getEmail(getAccoutProfile(request));
//		if(Utils.isNotEmpty(email)) {
//			if (!aclManager.userAllowed(domain, email)) {
//				throw new UnauthorizedException("Unauthorized Exception: role not valid");
//			}		
//		}
//
//		if (user.getEmail() == null) {
//			throw new BadRequestException("Missing email");
//		}	
//		if (user.getRole() == null) {
//			throw new BadRequestException("Missing role");
//		}			
//		
//		if (user.getDataset() != null && !UserRole.DATASET_USER.equals(user.getRole())) {
//			throw new UnauthorizedException("Bad role for dataset user");
//		}
//		if (user.getDataset() == null && UserRole.DATASET_USER.equals(user.getRole())) {
//			throw new UnauthorizedException("Bad role for domain user");
//		}		
//		user.setId(UUID.randomUUID().toString());
//		
//		
//		DomainConf dConf = dataManager.getDomainConf(domain);
//		if (dConf == null) {
//			throw new EntityNotFoundException("Domain not found");
//		}
//		
//		if (dConf.getUsers().contains(user)) {
//			throw new UnauthorizedException("User with same role already exist");
//		}
//		
//		dConf.getUsers().add(user);
//		dataManager.updateDomainConfUsers(dConf);
//		
//		if(logger.isInfoEnabled()) {
//			logger.info(String.format("addUser: %s", user.toString()));
//		}		
//		
//		return user;
//	}
//	
//	@RequestMapping(value = "/management/user/{domain}", method = RequestMethod.DELETE)
//	public @ResponseBody DomainConf deleteUser (
//			@PathVariable String domain,
//			HttpServletRequest request) throws Exception {
//		
//		String email = getEmail(getAccoutProfile(request));
//		if(Utils.isNotEmpty(email)) {
//			if (!aclManager.userAllowed(domain, email)) {
//				throw new UnauthorizedException("Unauthorized Exception: role not valid");
//			}		
//		}		
//		
//		DomainConf result = dataManager.removeDomainConf(domain);
//		if(logger.isInfoEnabled()) {
//			logger.info(String.format("deleteUser: %s", result.toString()));
//		}
//		return result;
//	}		
//	
	
//	@RequestMapping(value = "/management/user/{domain}/{dataset}", method = RequestMethod.PUT)
//	public @ResponseBody User updateDatasetUser (
//			@PathVariable String domain,
//			@PathVariable String dataset,			
//			@RequestBody User user,
//			HttpServletRequest request) throws Exception {
//
//		
//		String email = getEmail(getAccoutProfile(request));
//		if(Utils.isNotEmpty(email)) {
//			if (!aclManager.userAllowed(domain, email)) {
//				throw new UnauthorizedException("Unauthorized Exception: role not valid");
//			}		
//		}
//		
//		if (user.getEmail() == null) {
//			throw new UnauthorizedException("Missing email");
//		}			
//		if (user.getRole() != null && !UserRole.DATASET_USER.equals(user.getRole())) {
//			throw new UnauthorizedException("Bad role for dataset user");
//		}
//		user.setRole(UserRole.DATASET_USER);
//		
//		DataSetConf dsConf = dataManager.getDataSetConf(domain, dataset);
//		if (dsConf == null) {
//			throw new EntityNotFoundException("Dataset not found");
//		}
//		
//		if (dsConf.getUsers().contains(user)) {
//			throw new UnauthorizedException("User already exist");
//		}
//		
//		List<User> users = dsConf.getUsers();
//		
//		Optional<User> found = users.stream().filter(x -> x.getEmail().equals(user.getEmail())).findFirst();
//		if (!found.isPresent()) {
//			throw new EntityNotFoundException("User not found");
//		}
//		found.get().setRole(user.getRole());
//		
//		dataManager.updateDataSetConfUsers(dsConf);
//		
//		return found.get();
//	}	
//	
//	
//		@RequestMapping(value = "/management/user/{domain}", method = RequestMethod.POST)
//		public @ResponseBody User addDomainUser (
//				@PathVariable String domain,
//				@RequestBody User user,
//				HttpServletRequest request) throws Exception {
//
//			String email = getEmail(getAccoutProfile(request));
//			if(Utils.isNotEmpty(email)) {
//				if (!aclManager.userAllowed(domain, email)) {
//					throw new UnauthorizedException("Unauthorized Exception: role not valid");
//				}		
//			}
//			
//			if (user.getEmail() == null) {
//				throw new UnauthorizedException("Missing email");
//			}				
//			if (user.getRole() == null || UserRole.DATASET_USER.equals(user.getRole())) {
//				throw new UnauthorizedException("Bad role for domain user");
//			}
//			user.setId(UUID.randomUUID().toString());
//			
//			DomainConf dConf = dataManager.getDomainConf(domain);
//			if (dConf == null) {
//				throw new EntityNotFoundException("Domain not found");
//			}
//			
//			if (dConf.getUsers().contains(user)) {
//				throw new UnauthorizedException("User already exist");
//			}
//			
//			dConf.getUsers().add(user);
//			dataManager.updateDomainConfUsers(dConf);
//			
//			return user;
//		}	
//		
//		@RequestMapping(value = "/management/user/{domain}", method = RequestMethod.PUT)
//		public @ResponseBody User updateDomainUser (
//				@PathVariable String domain,
//
//				@RequestBody User user,
//				HttpServletRequest request) throws Exception {
//
//			String email = getEmail(getAccoutProfile(request));
//			if(Utils.isNotEmpty(email)) {
//				if (!aclManager.userAllowed(domain, email)) {
//					throw new UnauthorizedException("Unauthorized Exception: role not valid");
//				}		
//			}
//			
//			if (user.getEmail() == null) {
//				throw new UnauthorizedException("Missing email");
//			}			
//			if (user.getRole() == null || UserRole.DATASET_USER.equals(user.getRole())) {
//				throw new UnauthorizedException("Bad role for domain user");
//			}
//			
//			DomainConf dConf = dataManager.getDomainConf(domain);
//			if (dConf == null) {
//				throw new EntityNotFoundException("Domain not found");
//			}
//			
//			if (dConf.getUsers().contains(user)) {
//				throw new UnauthorizedException("User already exist");
//			}
//			
//			List<User> users = dConf.getUsers();
//			
//			Optional<User> found = users.stream().filter(x -> x.getEmail().equals(user.getEmail())).findFirst();
//			if (!found.isPresent()) {
//				throw new EntityNotFoundException("User not found");
//			}
//			found.get().setRole(user.getRole());
//			
//			dataManager.updateDomainConfUsers(dConf);
//			
//			return found.get();
//		}		
	
	
	//////////////
	// End CRUD //
	//////////////	
	
/*	private boolean checkUserEmail(String email, DataSetConf conf) {
		boolean result = false;
		List<ExternalUser> users = dataManager.findByDomain(conf.getDomain(), conf.getDataset());
		for(ExternalUser user : users) {
			if(email.equals(user.getEmail())) {
				result = true;
				break;
			}
		}
		return result;
	}
*/	
	@ExceptionHandler({EntityNotFoundException.class, StorageException.class, BadRequestException.class})
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleBadRequestError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}		
}
package it.smartcommunitylab.dsaengine.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.elastic.ElasticManager;
import it.smartcommunitylab.dsaengine.exception.BadRequestException;
import it.smartcommunitylab.dsaengine.exception.EntityNotFoundException;
import it.smartcommunitylab.dsaengine.exception.StorageException;
import it.smartcommunitylab.dsaengine.exception.UnauthorizedException;
import it.smartcommunitylab.dsaengine.kibana.utils.RolesUtils;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;

@Controller
public class DomainController extends AuthController {
	private static final String DSA_PROVIDER_ROLE_PREFIX = "DSA_PROVIDER_";

	private static final transient Logger logger = LoggerFactory.getLogger(DomainController.class);
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private ElasticManager elasticManager;
	
	@Autowired
	private RolesUtils aclManager;
	
//	@Autowired
//	private AuthManager authManager;
	
	// TODO clear elastic password?

	
	@RequestMapping(value = "/api/domain/{domain}/{dataset}/conf/user", method = RequestMethod.GET)
	public @ResponseBody DataSetConf getUserDataSetConfByDomain (
			@PathVariable String domain,
			@PathVariable String dataset,
			HttpServletRequest request) throws Exception {
		DataSetConf result = null;
		String email = getEmail(request);
		if(Utils.isNotEmpty(email)) {
//			DataSetConf conf = dataManager.getDataSetConf(domain, dataset);
//			if(checkUserEmail(email, conf)) {
//				result = conf;
//			}
			if (aclManager.isDatasetManagementAllowed(domain, email)) {
				DataSetConf conf = dataManager.getDataSetConf(domain, dataset);
				result = conf;
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUserDataSetConfByDomain: %s ", result));
		}
		return result;
	}
	
	// TODO: not CRUD?
	@RequestMapping(value = "/api/domain/{domain}/conf/user", method = RequestMethod.GET)
	public @ResponseBody List<DataSetConf> getUserDataSetConfs (
			@PathVariable String domain,
			HttpServletRequest request) throws Exception {
		String email = getEmail(request);
		String token = getAuthToken(request);
		List<DataSetConf> result = dataManager.getDataSetConf(domain, token, email);
//		List<String> userRoles = getUserRoles(token);
//		if((userRoles != null) && (userRoles.contains("dsa_" + domain.toLowerCase()))) {
//			DataSetConf dataSetConf = dataManager.getDataSetConf(domain, Const.DOMAIN_DATASET);
//			if(dataSetConf != null) {
//				result.add(dataSetConf);
//			}
//		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUserDataSetConfs: %s ", result));
		}
		return result;
	}
	
	// TODO: not CRUD?
	@RequestMapping(value = "/api/domain/{domain}/conf", method = RequestMethod.GET)
	public @ResponseBody List<DataSetConf> getDataSetConf (
			@PathVariable String domain,
			HttpServletRequest request) throws Exception {
		if(!checkRole("dsa_" + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<DataSetConf> result = dataManager.getDataSetConf(domain);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDataSetConf: %s ", result));
		}
		return result;
	}
	
	
	
	
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

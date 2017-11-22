package it.smartcommunitylab.dsaengine.controller;

import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.exception.EntityNotFoundException;
import it.smartcommunitylab.dsaengine.exception.StorageException;
import it.smartcommunitylab.dsaengine.exception.UnauthorizedException;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;

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

@Controller
public class RoleController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RepositoryManager dataManager;
	
	@RequestMapping(value = "/api/role/{domain}/{role}", method = RequestMethod.POST)
	public @ResponseBody void addDomainRole (
			@PathVariable String domain,
			@PathVariable String role,
			HttpServletRequest request) throws Exception {
	}
	
	@RequestMapping(value = "/api/role/{domain}/{dataset}/{role}", method = RequestMethod.POST)
	public @ResponseBody void addDatasetRole (
			@PathVariable String domain,
			@PathVariable String dataset,
			@PathVariable String role,
			HttpServletRequest request) throws Exception {
	}
	
	@RequestMapping(value = "/api/role/{domain}/{role}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteDomainRole (
			@PathVariable String domain,
			@PathVariable String role,
			HttpServletRequest request) throws Exception {
	}

	@RequestMapping(value = "/api/role/{domain}/{dataset}/{role}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteDatasetRole (
			@PathVariable String domain,
			@PathVariable String dataset,
			@PathVariable String role,
			HttpServletRequest request) throws Exception {
	}

	@ExceptionHandler({EntityNotFoundException.class, StorageException.class})
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
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

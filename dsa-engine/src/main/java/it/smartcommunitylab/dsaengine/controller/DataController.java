package it.smartcommunitylab.dsaengine.controller;

import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.elastic.ElasticManger;
import it.smartcommunitylab.dsaengine.exception.EntityNotFoundException;
import it.smartcommunitylab.dsaengine.exception.StorageException;
import it.smartcommunitylab.dsaengine.exception.UnauthorizedException;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class DataController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(DataController.class);
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private ElasticManger elasticManager;

	@RequestMapping(value = "/api/data/{domain}/{dataset}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> searchDataByDataset(
			@PathVariable String domain,
			@PathVariable String dataset,
			HttpServletRequest request) throws Exception {
		if(!checkRole("dsa_" + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		return elasticManager.searchData(domain, dataset, request.getParameterMap());
	}
	
	@RequestMapping(value = "/api/data/{domain}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> searchDataByDomain(
			@PathVariable String domain,
			HttpServletRequest request) throws Exception {
		if(!checkRole("dsa_" + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		return elasticManager.searchData(domain, request.getParameterMap());
	}
	
	@RequestMapping(value = "/api/index/{domain}/{dataset}/{type}", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> indexData(
			@PathVariable String domain,
			@PathVariable String dataset,
			@PathVariable String type,
			@RequestBody Map<String, Object> content,
			HttpServletRequest request) throws Exception {
		if(!checkRole("dsa_" + domain.toLowerCase(), request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		DataSetConf conf = dataManager.getDataSetConf(domain, dataset);
		return elasticManager.indexData(conf, type, content);
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

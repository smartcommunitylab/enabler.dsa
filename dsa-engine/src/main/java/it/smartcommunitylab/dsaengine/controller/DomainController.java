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
public class DomainController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(DomainController.class);
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private ElasticManger elasticManager;

	@RequestMapping(value = "/api/domain/{domain}/conf", method = RequestMethod.POST)
	public @ResponseBody DataSetConf addDataSetConf (
			@PathVariable String domain,
			@RequestBody DataSetConf conf,
			HttpServletRequest request) throws Exception {
		if(!checkRole("dsa_" + domain.toLowerCase())) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		conf.setDomain(domain);
		DataSetConf result = dataManager.addDataSetConf(conf);
		elasticManager.addIndex(conf);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addDataSetConf: %s", result.toString()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/domain/{domain}/{dataset}/conf", method = RequestMethod.PUT)
	public @ResponseBody DataSetConf updateDataSetConf (
			@PathVariable String domain,
			@PathVariable String dataset,
			@RequestBody DataSetConf conf,
			HttpServletRequest request) throws Exception {
		if(!checkRole("dsa_" + domain.toLowerCase())) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		conf.setDomain(domain);
		conf.setDataset(dataset);
		DataSetConf result = dataManager.updateDataSetConf(conf);
		elasticManager.addIndex(conf);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateDataSetConf: %s", result.toString()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/domain/{domain}/{dataset}/conf", method = RequestMethod.DELETE)
	public @ResponseBody DataSetConf deleteDataSetConf (
			@PathVariable String domain,
			@PathVariable String dataset,
			HttpServletRequest request) throws Exception {
		if(!checkRole("dsa_" + domain.toLowerCase())) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		DataSetConf result = dataManager.removeDataSetConf(domain, dataset);
		elasticManager.deleteIndex(domain, dataset);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteDataSetConf: %s", result.toString()));
		}
		return result;
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

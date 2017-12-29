package it.smartcommunitylab.dsa.client.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.dsa.client.common.Const;
import it.smartcommunitylab.dsa.client.common.IndexArchivePeriod;
import it.smartcommunitylab.dsa.client.common.IndexFormat;
import it.smartcommunitylab.dsa.client.common.Utils;
import it.smartcommunitylab.dsa.client.common.model.BaseDataSetConf;
import it.smartcommunitylab.dsa.client.common.model.BaseDomainConf;
import it.smartcommunitylab.dsa.client.common.model.ConfigurationProperties;
import it.smartcommunitylab.dsa.client.common.model.DataSetConf;
import it.smartcommunitylab.dsa.client.common.model.DomainConf;
import it.smartcommunitylab.dsa.client.common.model.Manager;
import it.smartcommunitylab.dsa.client.common.model.User;
import it.smartcommunitylab.dsa.client.exception.ServiceException;

public class DSAService {
	private String dsaURL;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public DSAService(String dsaURL) {
		this.dsaURL = dsaURL;
		if (!dsaURL.endsWith("/")) this.dsaURL += '/';
	}
	
	public BaseDataSetConf buildBaseDataSetConf(String dataset,
			IndexFormat indexFormat, IndexArchivePeriod indexArchivePeriod,
			int indexArchivePolicyPeriod, Map<String, Object> dataMapping) {
		BaseDataSetConf conf = new BaseDataSetConf();

		conf.setDataset(dataset);
		ConfigurationProperties cp = new ConfigurationProperties();
		if(indexFormat == IndexFormat.MONTHLY) {
			cp.setIndexFormat(Const.IDX_FORMAT_MONTHLY);
		} else {
			cp.setIndexFormat(Const.IDX_FORMAT_WEEKLY);
		}
		if(indexArchivePeriod == IndexArchivePeriod.MONTHS) {
			cp.setArchivePolicy(indexArchivePolicyPeriod + Const.IDX_ARCHIVE_POLICY_MONTHS);
		} else {
			cp.setArchivePolicy(indexArchivePolicyPeriod + Const.IDX_ARCHIVE_POLICY_DAYS);
		}
		cp.setDataMapping(dataMapping);
		conf.setConfigurationProperties(cp);
		return conf;
	}
	
	public BaseDomainConf buildBaseDomainConf(
			IndexFormat indexFormat, IndexArchivePeriod indexArchivePeriod,
			int indexArchivePolicyPeriod, Map<String, Object> dataMapping) {
		BaseDomainConf conf = new BaseDomainConf();

		ConfigurationProperties cp = new ConfigurationProperties();
		if(indexFormat == IndexFormat.MONTHLY) {
			cp.setIndexFormat(Const.IDX_FORMAT_MONTHLY);
		} else {
			cp.setIndexFormat(Const.IDX_FORMAT_WEEKLY);
		}
		if(indexArchivePeriod == IndexArchivePeriod.MONTHS) {
			cp.setArchivePolicy(indexArchivePolicyPeriod + Const.IDX_ARCHIVE_POLICY_MONTHS);
		} else {
			cp.setArchivePolicy(indexArchivePolicyPeriod + Const.IDX_ARCHIVE_POLICY_DAYS);
		}
		cp.setDataMapping(dataMapping);
		conf.setDefaultConfigurationProperties(cp);
		return conf;
	}	
	
	public DataSetConf addDataSetConf(String domain, BaseDataSetConf conf, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "datasets");
		HttpPost req = new HttpPost(url);
		String resp = executeRequest(req, conf, token);
    	DataSetConf data = mapper.readValue(resp, DataSetConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}
	
	public DataSetConf updateDataSetConf(String domain, BaseDataSetConf conf, String id, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "datasets", id);
		HttpPut req = new HttpPut(url);
		String resp = executeRequest(req, conf, token);
    	DataSetConf data = mapper.readValue(resp, DataSetConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
	
	public DataSetConf getDataSetConf(String domain, String id, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "datasets", id);
		HttpGet req = new HttpGet(url);
		String resp = executeRequest(req, null, token);
    	DataSetConf data = mapper.readValue(resp, DataSetConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
	
	public List<DataSetConf> getDataSetConfs(String domain, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "datasets");
		HttpGet req = new HttpGet(url);
		String resp = executeRequest(req, null, token);
		List<DataSetConf> data = mapper.readValue(resp, new TypeReference<ArrayList<DataSetConf>>() {});
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}		
	
	public DataSetConf deleteDataSetConf(String domain, String id,
	String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "datasets", id);
		HttpDelete req = new HttpDelete(url);
		String resp = executeRequest(req, null, token);
    	DataSetConf data = mapper.readValue(resp, DataSetConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	
	public DomainConf addDomainConf(String domain, BaseDomainConf conf, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "domains");
		HttpPost req = new HttpPost(url);
		String resp = executeRequest(req, conf, token);
		DomainConf data = mapper.readValue(resp, DomainConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}
	
	public DomainConf updateDomainConf(String domain, BaseDomainConf conf, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "domains");
		HttpPut req = new HttpPut(url);
		String resp = executeRequest(req, conf, token);
		DomainConf data = mapper.readValue(resp, DomainConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
	
	public DomainConf getDomainConf(String domain, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "domains");
		HttpGet req = new HttpGet(url);
		String resp = executeRequest(req, null, token);
		DomainConf data = mapper.readValue(resp, DomainConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}		
	
	public DomainConf deleteDomainConf(String domain,
	String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "domains");
		HttpDelete req = new HttpDelete(url);
		String resp = executeRequest(req, null, token);
		DomainConf data = mapper.readValue(resp, DomainConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}	
	
	public Manager addManager(String domain, Manager manager, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "managers");
		HttpPost req = new HttpPost(url);
		String resp = executeRequest(req, manager, token);
		Manager data = mapper.readValue(resp, Manager.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
		
	
	public Manager getManager(String domain, String managerId, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "managers", managerId);
		HttpGet req = new HttpGet(url);
		String resp = executeRequest(req, null, token);
		Manager data = mapper.readValue(resp, Manager.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}		
	
	public List<Manager> getManagers(String domain, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "managers");
		HttpGet req = new HttpGet(url);
		String resp = executeRequest(req, null, token);
		List<Manager> data = mapper.readValue(resp, new TypeReference<ArrayList<Manager>>() {});
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
	
	public DomainConf deleteManager(String domain, String managerId, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "managers", managerId);
		HttpDelete req = new HttpDelete(url);
		String resp = executeRequest(req, null, token);
		DomainConf data = mapper.readValue(resp, DomainConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
	
	
	public User addUser(String domain, User user, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "users");
		HttpPost req = new HttpPost(url);
		String resp = executeRequest(req, user, token);
		User data = mapper.readValue(resp, User.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
		
	
	public User getUser(String domain, String userId, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "users", userId);
		HttpGet req = new HttpGet(url);
		String resp = executeRequest(req, null, token);
		User data = mapper.readValue(resp, User.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}		
	
	public List<User> getUsers(String domain, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "users");
		HttpGet req = new HttpGet(url);
		String resp = executeRequest(req, null, token);
		List<User> data = mapper.readValue(resp, new TypeReference<ArrayList<User>>() {});
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
	
	public DomainConf deleteUser(String domain, String userId, String token) throws ServiceException {
		try {
		String url = buildUrl(domain, "users", userId);
		HttpDelete req = new HttpDelete(url);
		String resp = executeRequest(req, null, token);
		DomainConf data = mapper.readValue(resp, DomainConf.class);
    	return data;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}	
	
	
	private String buildUrl(String... parts) {
		StringBuilder sb = new StringBuilder(dsaURL + "management");
		for (String part: parts) {
			sb.append("/" + part);
		}
		return sb.toString();
	}
	
	private String executeRequest(HttpRequestBase request, Object entity, String token) throws Exception {
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Authorization", "Bearer " + token);
		if (entity != null) {
			((HttpEntityEnclosingRequestBase)request).setEntity(new StringEntity(mapper.writeValueAsString(entity), "UTF-8"));
		}
		HttpResponse resp = getHttpClient().execute(request);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	String responseJson = EntityUtils.toString(resp.getEntity());
	    	return responseJson;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }		
	}
	
	private Object invoke(HttpEntityEnclosingRequestBase request, Class clz) throws Exception {
		HttpResponse resp = getHttpClient().execute(request);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	Object data = mapper.readValue(responseJson, clz);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
	}
	
	
	public Map<String, Object> searchDataByDataset(String domain, String dataset, 
			Map<String, String[]> params, String token) throws ServiceException {
		try {
			final HttpResponse resp;
			String url = dsaURL + "api/data/" + domain;
			if(Utils.isNotEmpty(dataset)) {
				url = url + "/" + dataset; 
			}
	    final HttpGet get = new HttpGet(url);
	    get.setHeader("Accept", "application/json");
	    get.setHeader("Content-Type", "application/json");
	    get.setHeader("Authorization", "Bearer " + token);
			if(params != null) {
				url += "?";
				for(String paramName : params.keySet()) {
					String[] paramValueList = params.get(paramName);
					for(String paramValue : paramValueList) {
						url += paramName + "=" + URLEncoder.encode(paramValue, "UTF-8") + "&"; 
					}
				}
				url = url.substring(0, url.length()-1);
			}
	    resp = getHttpClient().execute(get);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	TypeReference<HashMap<String,Object>> typeRef = 
	    			new TypeReference<HashMap<String,Object>>() {};
	    	Map<String, Object> data = mapper.readValue(responseJson, typeRef);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public Map<String, Object> searchDataByDomain(String domain,  
			Map<String, String[]> params, String token) throws ServiceException {
		return searchDataByDataset(domain, null, params, token);
	}
	
	public Map<String, Object> indexData(String domain, String dataset, 
			String dataType, Map<String, Object> content, String token) throws ServiceException {
		try {
			final HttpResponse resp;
	    String url = dsaURL + "api/index/" + domain + "/" + dataset
	    		+ "/" + dataType;
	    final HttpPost post = new HttpPost(url);
	    post.setHeader("Accept", "application/json");
	    post.setHeader("Content-Type", "application/json");
	    post.setHeader("Authorization", "Bearer " + token);
	    post.setEntity(new StringEntity(mapper.writeValueAsString(content), "UTF-8"));
	    resp = getHttpClient().execute(post);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	TypeReference<HashMap<String,Object>> typeRef = 
	    			new TypeReference<HashMap<String,Object>>() {};
	    			Map<String, Object> data = mapper.readValue(responseJson, typeRef);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
  private HttpClient getHttpClient() {
    HttpClient httpClient = HttpClientBuilder.create().build();
    return httpClient;
  }	

}

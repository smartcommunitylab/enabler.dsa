package it.smartcommunitylab.dsa.client.service;

import it.smartcommunitylab.dsa.client.common.Const;
import it.smartcommunitylab.dsa.client.common.IndexArchivePeriod;
import it.smartcommunitylab.dsa.client.common.IndexFormat;
import it.smartcommunitylab.dsa.client.common.Utils;
import it.smartcommunitylab.dsa.client.common.model.DataSetConf;
import it.smartcommunitylab.dsa.client.common.model.ExternalUser;
import it.smartcommunitylab.dsa.client.exception.ServiceException;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DSAService {
	private String dsaURL;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public DSAService(String dsaURL) {
		this.dsaURL = dsaURL;
		if (!dsaURL.endsWith("/")) this.dsaURL += '/';
	}
	
	public DataSetConf buildDataSetConf(String domain, String dataset,
			IndexFormat indexFormat, IndexArchivePeriod indexArchivePeriod,
			int indexArchivePolicyPeriod, Map<String, Object> dataMapping) {
		DataSetConf conf = new DataSetConf();
		conf.setDomain(domain);
		conf.setDataset(dataset);
		if(indexFormat == IndexFormat.MONTHLY) {
			conf.setIndexFormat(Const.IDX_FORMAT_MONTHLY);
		} else {
			conf.setIndexFormat(Const.IDX_FORMAT_WEEKLY);
		}
		if(indexArchivePeriod == IndexArchivePeriod.MONTHS) {
			conf.setArchivePolicy(indexArchivePolicyPeriod + Const.IDX_ARCHIVE_POLICY_MONTHS);
		} else {
			conf.setArchivePolicy(indexArchivePolicyPeriod + Const.IDX_ARCHIVE_POLICY_DAYS);
		}
		conf.setDataMapping(dataMapping);
		return conf;
	}
	
	public List<DataSetConf> getUserDataSetConf(String domain, String email, 
			String token) throws ServiceException {
		try {
			final HttpResponse resp;
	    String url = dsaURL + "api/domain/" + domain + "/conf/user?email=" + URLEncoder.encode(email, "UTF-8");
	    final HttpGet post = new HttpGet(url);
	    post.setHeader("Accept", "application/json");
	    post.setHeader("Content-Type", "application/json");
	    post.setHeader("Authorization", "Bearer " + token);
	    resp = getHttpClient().execute(post);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	List<DataSetConf> data = mapper.readValue(responseJson, new TypeReference<List<DataSetConf>>(){});
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}
	
	public DataSetConf getDataSetConf(String domain, String dataset, 
			String token) throws ServiceException {
		try {
			final HttpResponse resp;
	    String url = dsaURL + "api/domain/" + domain + "/" + dataset + "/conf";
	    final HttpGet post = new HttpGet(url);
	    post.setHeader("Accept", "application/json");
	    post.setHeader("Content-Type", "application/json");
	    post.setHeader("Authorization", "Bearer " + token);
	    resp = getHttpClient().execute(post);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	DataSetConf data = mapper.readValue(responseJson, DataSetConf.class);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}
	
	public DataSetConf addDataSetConf(DataSetConf conf, String token) 
			throws ServiceException {
		try {
			final HttpResponse resp;
	    String url = dsaURL + "api/domain/" + conf.getDomain() + "/conf";
	    final HttpPost post = new HttpPost(url);
	    post.setHeader("Accept", "application/json");
	    post.setHeader("Content-Type", "application/json");
	    post.setHeader("Authorization", "Bearer " + token);
	    post.setEntity(new StringEntity(mapper.writeValueAsString(conf), "UTF-8"));
	    resp = getHttpClient().execute(post);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	DataSetConf data = mapper.readValue(responseJson, DataSetConf.class);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public DataSetConf updateDataSetConf(DataSetConf conf, String token) 
			throws ServiceException {
		try {
			final HttpResponse resp;
	    String url = dsaURL + "api/domain/" + conf.getDomain() 
	    		+ "/" + conf.getDataset() + "/conf";
	    final HttpPut put = new HttpPut(url);
	    put.setHeader("Accept", "application/json");
	    put.setHeader("Content-Type", "application/json");
	    put.setHeader("Authorization", "Bearer " + token);
	    put.setEntity(new StringEntity(mapper.writeValueAsString(conf), "UTF-8"));
	    resp = getHttpClient().execute(put);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	DataSetConf data = mapper.readValue(responseJson, DataSetConf.class);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public DataSetConf deleteDataSetConf(String domain, String dataset,
			String token) throws ServiceException {
		try {
			final HttpResponse resp;
	    String url = dsaURL + "api/domain/" + domain 
	    		+ "/" + dataset + "/conf";
	    final HttpDelete delete = new HttpDelete(url);
	    delete.setHeader("Accept", "application/json");
	    delete.setHeader("Content-Type", "application/json");
	    delete.setHeader("Authorization", "Bearer " + token);
	    resp = getHttpClient().execute(delete);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	DataSetConf data = mapper.readValue(responseJson, DataSetConf.class);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public DataSetConf setDataSetConfUsers(String domain, String dataset,
			List<ExternalUser> users, String token) throws ServiceException {
		try {
			final HttpResponse resp;
	    String url = dsaURL + "api/domain/" + domain + "/" + dataset + "/users";
	    final HttpPut put = new HttpPut(url);
	    put.setHeader("Accept", "application/json");
	    put.setHeader("Content-Type", "application/json");
	    put.setHeader("Authorization", "Bearer " + token);
	    put.setEntity(new StringEntity(mapper.writeValueAsString(users), "UTF-8"));
	    resp = getHttpClient().execute(put);
	    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	final String responseJson = EntityUtils.toString(resp.getEntity());
	    	DataSetConf data = mapper.readValue(responseJson, DataSetConf.class);
	    	return data;
	    } else {
	    	throw new ServiceException("Error in service invocation:" 
	    			+ resp.getStatusLine());
	    }
		} catch (Exception e) {
			throw new ServiceException(e);
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

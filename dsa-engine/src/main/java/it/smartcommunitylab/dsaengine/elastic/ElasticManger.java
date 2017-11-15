package it.smartcommunitylab.dsaengine.elastic;

import it.smartcommunitylab.dsaengine.common.Const;
import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.utils.HTTPUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ElasticManger {
	@Autowired
	@Value("${elastic.endpoint}")
	private String endpoint;
	
	@Autowired
	@Value("${elastic.user}")
	private String adminUser;
	
	@Autowired
	@Value("${elastic.password}")
	private String adminPassword;
	
	public final String indexDateMonthly = "yyyy-MM";
	public final String indexDateWeekly = "yyyy-MM-ww";
	
	public final SimpleDateFormat sdfMonthly = new SimpleDateFormat(indexDateMonthly);
	public final SimpleDateFormat sdfWeekly = new SimpleDateFormat(indexDateWeekly);
	
	private ObjectMapper fullMapper;
	
	public ElasticManger() {
		fullMapper = new ObjectMapper();
		fullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		fullMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		fullMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		fullMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		fullMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}
	
	private String getDate(DataSetConf conf) {
		String result = "";
		Date now = new Date();
		if(conf.getIndexFormat().equals(Const.IDX_MONTHLY)) {
			result = sdfMonthly.format(now);
		} else if(conf.getIndexFormat().equals(Const.IDX_WEEKLY)) {
			result = sdfWeekly.format(now);
		}
		return result;
	}

	
	public Map<String, Object> searchData(String domain, String dataset, 
			Map<String, String[]> params) throws Exception {
		String address = endpoint;
		if(Utils.isNotEmpty(dataset)) {
			address += domain.toLowerCase() + "-" + dataset.toLowerCase() + "-*/_search";
		} else {
			address += domain.toLowerCase() + "-*/_search";
		}
		return HTTPUtils.send(address, "GET", params, null, adminUser, adminPassword);
	}
	
	public Map<String, Object> searchData(String domain, Map<String, String[]> params) 
			throws Exception {
		return searchData(domain, null, params);
	}
	
	public Map<String, Object> indexData(DataSetConf conf, String type, 
			Map<String, Object> content) throws Exception {
		String address = endpoint + conf.getDomain().toLowerCase() + "-" 
			+ conf.getDataset().toLowerCase() + "-" + getDate(conf) + "/" + type;
		return HTTPUtils.send(address, "POST", null, content, adminUser, adminPassword);
	}
	
	public Map<String, Object> addIndex(DataSetConf conf) throws Exception {
		String indexTemplate = conf.getDomain().toLowerCase() 
				+ "-" + conf.getDataset().toLowerCase() + "-*";
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("template", indexTemplate);
		content.put("mappings", conf.getDataMapping());
		String address = endpoint + "_template/" + conf.getDomain().toLowerCase() 
				+ "-" + conf.getDataset().toLowerCase() + "-template";
		return HTTPUtils.send(address, "PUT", null, content, adminUser, adminPassword);
	}
	
	public Map<String, Object> deleteIndex(String domain, String dataset) throws Exception {
		String address = endpoint + domain.toLowerCase() 
				+ "-" + dataset.toLowerCase() + "-*";
		return HTTPUtils.send(address, "DELETE", null, null, adminUser, adminPassword);
	}

	public List<String> getIndexes(String domain, String dataset) throws Exception {
		String address = endpoint + domain.toLowerCase() + "-" + dataset.toLowerCase() + "-*";
		Map<String, Object> response = HTTPUtils.send(address, "GET", null, null, adminUser, adminPassword);
		return new ArrayList<String>(response.keySet());
	}

	public Map<String, Object> closeIndex(String indexName) throws Exception {
		String address = endpoint + indexName.toLowerCase() + "/_close";
		return HTTPUtils.send(address, "POST", null, null, adminUser, adminPassword);
	}
	
	public Map<String, Object> addRole(DataSetConf conf) throws Exception {
		String index = conf.getDomain().toLowerCase() + "-" + conf.getDataset().toLowerCase() + "-*";		
		Map<String, Object> content = new HashMap<String, Object>();
		List<Map<String, Object>> indices = new ArrayList<Map<String,Object>>();
		Map<String, Object> roleIndex = new HashMap<String, Object>();
		roleIndex.put("names", new String[] {index});
		roleIndex.put("privileges", new String[] {"read", "write", "view_index_metadata"});
		indices.add(roleIndex);
		Map<String, Object> roleKibana = new HashMap<String, Object>();
		roleKibana.put("names", new String[] {".kibana*"});
		roleKibana.put("privileges", new String[] {"read", "view_index_metadata"});
		indices.add(roleKibana);
		content.put("indices", indices);
		String address = endpoint + "_xpack/security/role/" + conf.getElasticUser();
		return HTTPUtils.send(address, "POST", null, content, adminUser, adminPassword);
	}
	
	public Map<String, Object> deleteRole(DataSetConf conf) throws Exception {
		String address = endpoint + "_xpack/security/role/" + conf.getElasticUser();
		return HTTPUtils.send(address, "DELETE", null, null, adminUser, adminPassword);
	}
	
	public Map<String, Object> addUser(DataSetConf conf) throws Exception {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("password", conf.getElasticPassword());
		content.put("roles", new String[] {conf.getElasticUser()});
		content.put("enabled", Boolean.TRUE);
		String address = endpoint + "_xpack/security/user/" + conf.getElasticUser();
		return HTTPUtils.send(address, "POST", null, content, adminUser, adminPassword);
	}

	public Map<String, Object> deleteUser(DataSetConf conf) throws Exception {
		String address = endpoint + "_xpack/security/user/" + conf.getElasticUser();
		return HTTPUtils.send(address, "DELETE", null, null, adminUser, adminPassword);
	}

	public void addDomainRole(DataSetConf conf) throws Exception {
		String index = conf.getDomain().toLowerCase() + "-*";		
		Map<String, Object> content = new HashMap<String, Object>();
		List<Map<String, Object>> indices = new ArrayList<Map<String,Object>>();
		Map<String, Object> roleIndex = new HashMap<String, Object>();
		roleIndex.put("names", new String[] {index});
		roleIndex.put("privileges", new String[] {"read", "write", "view_index_metadata"});
		indices.add(roleIndex);
		Map<String, Object> roleKibana = new HashMap<String, Object>();
		roleKibana.put("names", new String[] {".kibana*"});
		roleKibana.put("privileges", new String[] {"read", "view_index_metadata"});
		indices.add(roleKibana);
		content.put("indices", indices);
		String address = endpoint + "_xpack/security/role/" + conf.getElasticUser();
		HTTPUtils.send(address, "POST", null, content, adminUser, adminPassword);
	}

	public void addDomainUser(DataSetConf conf) throws Exception {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("password", conf.getElasticPassword());
		content.put("roles", new String[] {conf.getElasticUser()});
		content.put("enabled", Boolean.TRUE);
		String address = endpoint + "_xpack/security/user/" + conf.getElasticUser();
		HTTPUtils.send(address, "POST", null, content, adminUser, adminPassword);
	}

}

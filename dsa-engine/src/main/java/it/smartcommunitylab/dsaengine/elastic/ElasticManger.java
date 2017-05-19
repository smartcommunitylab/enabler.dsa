package it.smartcommunitylab.dsaengine.elastic;

import it.smartcommunitylab.dsaengine.common.Utils;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.utils.HTTPUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
	
	private SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM");
	
	private ObjectMapper fullMapper;
	
	public ElasticManger() {
		fullMapper = new ObjectMapper();
		fullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		fullMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		fullMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		fullMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		fullMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
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
	
	public Map<String, Object> indexData(String domain, String dataset, String type, 
			Map<String, Object> content) throws Exception {
		String address = endpoint + domain.toLowerCase() + "-" + dataset.toLowerCase() 
				+ "-" + sdf.format(new Date()) + "/" + type;
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
}

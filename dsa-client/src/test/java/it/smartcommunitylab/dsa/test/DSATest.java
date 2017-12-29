package it.smartcommunitylab.dsa.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.smartcommunitylab.dsa.client.common.IndexArchivePeriod;
import it.smartcommunitylab.dsa.client.common.IndexFormat;
import it.smartcommunitylab.dsa.client.common.model.BaseDataSetConf;
import it.smartcommunitylab.dsa.client.common.model.BaseDomainConf;
import it.smartcommunitylab.dsa.client.common.model.DataSetConf;
import it.smartcommunitylab.dsa.client.common.model.DomainConf;
import it.smartcommunitylab.dsa.client.common.model.Manager;
import it.smartcommunitylab.dsa.client.common.model.User;
import it.smartcommunitylab.dsa.client.service.DSAService;

public class DSATest {

	private static final String DOMAIN = "test_domain";
	private static final String DATASET = "test_dataset";
	private static final String MANAGER = "admin";
	private static final String USER = "admin";
	
	private String dsaURL = "http://localhost:6030/dsa-engine/";
	private String token = "xxxxxxxxxxxxxxxxxxx";
	
	private DSAService dsaService;
	
	private DomainConf domain;
	private DataSetConf dataset;
	
	@Before
	public void setup() throws Exception {
		dsaService = new DSAService(dsaURL);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ObjectNode props = root.putObject("_default_").putObject("properties");
		props.putObject("logLevel").put("type", "text");
		props.putObject("routeId").put("type", "text");
		props.putObject("timestamp").put("type", "date");
		props.putObject("eventType").put("type", "text");
		Map<String, Object> map = mapper.convertValue(root, Map.class);
		
		BaseDomainConf dConf = dsaService.buildBaseDomainConf(IndexFormat.MONTHLY, IndexArchivePeriod.DAYS, 10, map);
		domain = dsaService.addDomainConf(DOMAIN, dConf, token);
		
		BaseDataSetConf dsConf = dsaService.buildBaseDataSetConf(DATASET, 
				IndexFormat.MONTHLY, IndexArchivePeriod.DAYS, 10, map);
		dataset = dsaService.addDataSetConf(DOMAIN, dsConf, token);	
	}
	
	@After
	public void removeDataSet() throws Exception {
		dsaService.deleteDataSetConf(DOMAIN, dataset.getId(), token);
		dsaService.deleteDomainConf(DOMAIN, token);
	}
	
	
	@Test
	public void testIndex() throws Exception {
		System.err.println("INDEX");
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("logLevel", "ERROR");
		content.put("routeId", "2");
		content.put("timestamp", new Date());
		content.put("eventType", "CHECKIN");
		Map<String, Object> result = dsaService.indexData(DOMAIN, DATASET, "event", content, token);
		System.out.println(result);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void testSearch() throws Exception {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("q", new String[] {"eventType:CHECKIN"});
		Map<String, Object> result = dsaService.searchDataByDataset(DOMAIN, DATASET, params, token);
		System.out.println(result);
		Assert.assertNotNull(result);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDomainConf() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ObjectNode props = root.putObject("_default_").putObject("properties");
		props.putObject("logLevel").put("type", "text");
		props.putObject("routeId").put("type", "text");
		props.putObject("timestamp").put("type", "date");
		props.putObject("eventType").put("type", "text");
		Map<String, Object> map = mapper.convertValue(root, Map.class);
		
		BaseDomainConf dConf = dsaService.buildBaseDomainConf(IndexFormat.MONTHLY, IndexArchivePeriod.DAYS, 10, map);		
		DomainConf result = dsaService.updateDomainConf(DOMAIN, dConf, token);
		
		Manager manager = new Manager();
		manager.setEmail(MANAGER);
		Manager man = dsaService.addManager(DOMAIN, manager, token);
		List<Manager> managers = dsaService.getManagers(DOMAIN, token);
		Assert.assertEquals(2, managers.size());
		dsaService.getManager(DOMAIN, man.getId(), token);
		result = dsaService.deleteManager(DOMAIN, man.getId(), token);
		Assert.assertEquals(1, result.getManagers().size());
		
		User user = new User();
		user.setEmail(USER);
		user.setDataset(DATASET);
		User usr = dsaService.addUser(DOMAIN, user, token);
		List<User> users = dsaService.getUsers(DOMAIN, token);
		Assert.assertEquals(1, users.size());
		dsaService.getUser(DOMAIN, usr.getId(), token);
		result = dsaService.deleteUser(DOMAIN, usr.getId(), token);
		Assert.assertEquals(0, result.getUsers().size());		
		
		result = dsaService.getDomainConf(DOMAIN, token);
		Assert.assertNotNull(result);
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDataSetConf() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ObjectNode props = root.putObject("_default_").putObject("properties");
		props.putObject("logLevel").put("type", "text");
		props.putObject("routeId").put("type", "text");
		props.putObject("timestamp").put("type", "date");
		props.putObject("eventType").put("type", "text");
		Map<String, Object> map = mapper.convertValue(root, Map.class);
		BaseDataSetConf conf = dsaService.buildBaseDataSetConf(DATASET, 
				IndexFormat.MONTHLY, IndexArchivePeriod.DAYS, 10, map);		
		DataSetConf result = dsaService.updateDataSetConf(DOMAIN, conf, dataset.getId(), token);
		result = dsaService.getDataSetConf(DOMAIN, dataset.getId(), token);
		List<DataSetConf> resultList  = dsaService.getDataSetConfs(DOMAIN, token);

	}	
	
}

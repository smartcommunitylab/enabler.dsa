package it.smartcommunitylab.dsa.test;

import it.smartcommunitylab.dsa.client.common.IndexArchivePeriod;
import it.smartcommunitylab.dsa.client.common.IndexFormat;
import it.smartcommunitylab.dsa.client.common.model.DataSetConf;
import it.smartcommunitylab.dsa.client.common.model.ExternalUser;
import it.smartcommunitylab.dsa.client.service.DSAService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DSATest {
	private String dsaURL = "http://localhost:6030/dsa-engine/";
	private String token = "xxxxxxxxxxxxxxxxxxx";
	
	private DSAService dsaService;
	
	@Before
	public void setup() {
		dsaService = new DSAService(dsaURL);
	}
	
	@Test
	public void testIndex() throws Exception {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("logLevel", "ERROR");
		content.put("routeId", "2");
		content.put("timestamp", new Date());
		content.put("eventType", "CHECKIN");
		Map<String, Object> result = dsaService.indexData("climb", "VELA", "event", content, token);
		System.out.println(result);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void testSearch() throws Exception {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("q", new String[] {"eventType:CHECKIN"});
		Map<String, Object> result = dsaService.searchDataByDataset("climb", "VELA", params, token);
		System.out.println(result);
		Assert.assertNotNull(result);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAddConf() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ObjectNode props = root.putObject("_default_").putObject("properties");
		props.putObject("logLevel").put("type", "string");
		props.putObject("routeId").put("type", "string");
		props.putObject("timestamp").put("type", "date");
		props.putObject("eventType").put("type", "string");
		Map<String, Object> map = mapper.convertValue(root, Map.class);
		DataSetConf conf = dsaService.buildDataSetConf("climb", "VELA", 
				IndexFormat.MONTHLY, IndexArchivePeriod.DAYS, 10, map);
		dsaService.addDataSetConf(conf, token);
	}
	
	@Test
	public void deleteConf() throws Exception {
		dsaService.deleteDataSetConf("climb", "VELA", token);
		dsaService.deleteDataSetConf("climb", "all", token);
	}
	
	@Test
	public void testAddUser() throws Exception {
		List<ExternalUser> users = new ArrayList<ExternalUser>();
		ExternalUser user = new ExternalUser();
		user.setEmail("test@test.com");
		users.add(user);
		dsaService.setDataSetConfUsers("climb", "VELA", users, token);
	}
	
}

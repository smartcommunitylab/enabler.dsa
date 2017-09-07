package it.smartcommunitylab.dsa.test;

import it.smartcommunitylab.dsa.client.service.DSAService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DSATest {
	private String dsaURL = "http://localhost:6030/dsa-engine/";
	private String token = "cc0b96e9-53f9-471a-b080-feae831efe1a";
	
	private DSAService dsaService;
	
	@Before
	public void setup() {
		dsaService = new DSAService(dsaURL);
	}
	
	@Test
	public void testIndex() throws Exception {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("logLevel", "INFO");
		content.put("routeId", "1");
		content.put("timestamp", new Date());
		content.put("eventType", "CHECKIN");
		content.put("payload", "payload_test");
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

}
